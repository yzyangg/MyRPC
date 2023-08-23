package com.yzy.trans.client;

import com.yzy.loadbalancer.LoadBalancer;
import com.yzy.loadbalancer.RandomLoadBalancer;
import com.yzy.registry.NacosServiceDiscovery;
import com.yzy.registry.ServiceDiscovery;
import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.entity.RpcResponse;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import com.yzy.rpc.factory.SingletonFactory;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.RpcClient;
import com.yzy.trans.dto.UnprocessedRequests;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 17:36
 */
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;


    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class);
    }

    /**
     * 服务发现
     */
    private ServiceDiscovery serviceDiscovery;
    /**
     * 序列化方式
     */
    private CommonSerializer serializer;

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new NacosServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    /**
     * 未处理的请求
     */
    private UnprocessedRequests unprocessedRequests;

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest, String serviceName) {
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookUpService(serviceName);
            if (inetSocketAddress == null) {
                logger.error("找不到对应的服务: {}", serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND, serviceName);
            }


            // 获取channel
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (channel == null || !channel.isActive()) {
                logger.error("未连接到服务器，发送消息失败");
                group.shutdownGracefully();
                return null;
            }

            unprocessedRequests.put(rpcRequest.getRequestId(), future);

            //发送请求，添加回调函数
            channel.writeAndFlush(rpcRequest).addListener(
                    // 异步发送消息
                    (ChannelFutureListener) one -> {
                        if (one.isSuccess()) {
                            logger.info("客户端发送消息: {}", rpcRequest);
                        } else {
                            logger.error("发送消息时有错误发生: ", one.cause());
                            one.channel().close();
                            future.completeExceptionally(one.cause());
                        }
                    });
        } catch (Exception e) {
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error("发送消息时有错误发生: ", e);
            Thread.currentThread().interrupt();
        }


        return future;
    }
}

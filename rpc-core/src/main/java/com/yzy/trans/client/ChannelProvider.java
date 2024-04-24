package com.yzy.trans.client;

import com.yzy.codec.CommonDecoder;
import com.yzy.codec.CommonEncoder;
import com.yzy.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author yzy
 * @version 1.0
 * @description 获取channel对象
 * @date 2023/8/22 17:36
 */
public class ChannelProvider {
    public static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static final Bootstrap bootstrap = initializeBootstrap();

    /**
     * 存放channel,服务端地址 + 序列化方式 -> channel
     */
    public static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    /**
     * 获取channel对象
     *
     * @param inetSocketAddress 服务端地址
     * @param serializer        序列化方式
     * @return channel
     */
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {

        // 生成key 地址 + 序列化方式
        String key = inetSocketAddress.toString() + serializer.getCode();

        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            // 如果channel可用，直接返回
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                // 如果channel不可用，从map中移除
                channels.remove(key);
            }
        }

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                socketChannel.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });

        Channel channel;
        try {
            // 获得建立连接后的channel
            channel = connect(inetSocketAddress);
        } catch (Exception e) {
            logger.error("连接客户端时有错误发生", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    /**
     * 连接服务端，添加监听器，返回channel
     *
     * @param inetSocketAddress 服务端地址
     * @return channel
     */
    private static Channel connect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        // 异步连接服务端 返回channel
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                logger.info("客户端连接成功");
                completableFuture.complete(channelFuture.channel());
            } else {
                logger.error("客户端连接失败");
                throw new IllegalStateException();
            }
        });
        // 返回channel
        return completableFuture.get();
    }

    /**
     * 初始化 Bootstrap
     *
     * @return Bootstrap
     */
    private static Bootstrap initializeBootstrap() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 保持长连接，激活心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 连接超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 禁用 Nagle 算法
                .option(ChannelOption.TCP_NODELAY, true);

        return bootstrap;
    }


}

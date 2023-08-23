package com.yzy.trans.client;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.entity.RpcResponse;
import com.yzy.rpc.factory.SingletonFactory;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.dto.UnprocessedRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author yzy
 * @version 1.0
 * @description 客户端处理器（指定泛型为RpcResponse，表示只处理RpcResponse类型的数据）
 * @date 2023/8/22 17:37
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private final UnprocessedRequests unprocessedRequests;

    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {

        try {
            logger.info(String.format("客户端接收到消息: %s", msg));
            //读到了消息就代表该类已经被处理了，设置值到future中
            unprocessedRequests.complete(msg);
        } catch (Exception e) {
            // 释放资源 (引用计数法)
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发生:");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("发送心跳包 [{}]", ctx.channel().remoteAddress());
                // 获取channel
                Channel channel = ChannelProvider.get((InetSocketAddress) ctx.channel().remoteAddress()
                        , Objects.requireNonNull(CommonSerializer.getByCode(CommonSerializer.DEFAULT_SERIALIZER)));
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setHeartBeat(true);
                if (channel != null) {
                    // 发送心跳包
                    channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }

            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}

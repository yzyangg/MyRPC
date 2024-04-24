package com.yzy.trans.server;

import com.yzy.codec.CommonDecoder;
import com.yzy.codec.CommonEncoder;
import com.yzy.hook.ShutDownHook;
import com.yzy.provider.ServiceProviderImpl;
import com.yzy.registry.NacosServiceRegistry;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import static com.yzy.serializer.CommonSerializer.DEFAULT_SERIALIZER;

/**
 * @author yzy
 * @version 1.0
 * @description NIO服务端
 * @date 2023/8/22 17:35
 */
public class NettyServer extends AbstractRpcServer {
    private final CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanService();
    }

    @Override
    public void start() {
        ShutDownHook.getShutDownHook().addClearAllHock();
        // boss 负责接受连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // worker 负责读写
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        // Server启动器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new IdleStateHandler(300000, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new CommonDecoder())
                                    .addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future;
            // 阻塞直到绑定成功
            future = serverBootstrap.bind(host, port).sync();
            logger.info("服务器启动在{}端口", port);
            // 阻塞直到服务器channel关闭
            future.channel().closeFuture().sync();
            logger.info("服务器在{}端口已关闭", port);
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
            throw new RpcException(RpcError.SERVER_STARTUP_FAILURE);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

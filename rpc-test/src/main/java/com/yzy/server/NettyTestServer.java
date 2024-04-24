package com.yzy.server;

import com.yzy.annotation.ServiceScan;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.server.NettyServer;

/**
 * @author yzy
 * @version 1.0
 * @description nettytestserver
 * @date 2023/8/23 11:32
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        // 扫描@Service服务 通过NamingService注册到Nacos
        NettyServer nettyServer = new NettyServer("localhost", 9999, CommonSerializer.DEFAULT_SERIALIZER);

        // 通过Bootstrap启动 创建BossEventGroup WorkerEventGroup
        nettyServer.start();
    }
}

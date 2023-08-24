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
        NettyServer nettyServer = new NettyServer("localhost", 9999, CommonSerializer.DEFAULT_SERIALIZER);
        nettyServer.start();
    }
}

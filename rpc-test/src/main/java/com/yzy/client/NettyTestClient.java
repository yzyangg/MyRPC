package com.yzy.client;

import com.yzy.api.HelloObject;
import com.yzy.api.HelloService;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.RpcClient;
import com.yzy.trans.RpcClientProxy;
import com.yzy.trans.client.NettyClient;

/**
 * @author yzy
 * @version 1.0
 * @description nettyTestClient
 * @date 2023/8/23 11:15
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.DEFAULT_SERIALIZER);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(12, "This is a message");
        String res = helloService.hello(message);
        System.out.println(res);
    }
}

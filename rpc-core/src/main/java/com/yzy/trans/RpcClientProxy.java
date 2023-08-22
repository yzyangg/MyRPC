package com.yzy.trans;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 18:18
 */
public class RpcClientProxy implements InvocationHandler {
    public static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    /**
     * 获取代理对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(),
                method.getDeclaringClass().getName(),
                method.getName(),
                args, method.getParameterTypes(), false);

        RpcResponse rpcResponse = null;
        CompletableFuture<RpcResponse> future = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest, method.getDeclaringClass().getName());
        try {
            rpcResponse = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("方法调用请求发送失败", e);
            return null;
        }

        return null;
    }
}

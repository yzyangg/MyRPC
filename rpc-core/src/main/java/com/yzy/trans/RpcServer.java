package com.yzy.trans;

import com.yzy.serializer.CommonSerializer;

/**
 * @author yzy
 * @version 1.0
 * @description 服务端接口
 * @date 2023/8/22 18:14
 */
public interface RpcServer {
    int DEFAULT_SERVER_PORT = CommonSerializer.DEFAULT_SERIALIZER;

    /**
     * 启动服务端
     */
    void start();

    /**
     * 发布服务
     *
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void publishService(T service, String serviceName);
}

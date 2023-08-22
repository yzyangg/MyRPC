package com.yzy.registry;

import java.net.InetSocketAddress;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 17:14
 */
public interface ServiceRegistry {
    /**
     * 注册服务
     *
     * @param serviceName       服务名
     * @param inetSocketAddress 服务地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}

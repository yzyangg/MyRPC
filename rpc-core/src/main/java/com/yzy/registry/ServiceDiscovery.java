package com.yzy.registry;

import java.net.InetSocketAddress;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 17:15
 */
public interface ServiceDiscovery {
    /**
     * 根据服务名返回服务地址
     *
     * @param serviceName 服务名
     * @return 服务地址
     */
    InetSocketAddress lookUpService(String serviceName);
}

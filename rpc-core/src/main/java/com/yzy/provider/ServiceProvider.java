package com.yzy.provider;

/**
 * @author yzy
 * @version 1.0
 * @description 保存和提供服务的实例对象
 * @date 2023/8/22 16:18
 */
public interface ServiceProvider {
    /**
     * 添加服务
     *
     * @param service
     * @param serviceName
     * @param <T>
     */
    <T> void addServiceProvider(T service, String serviceName);

    /**
     * 根据服务名获取服务实例对象
     *
     * @param serviceName
     * @return
     */
    Object getServiceProvider(String serviceName);

}

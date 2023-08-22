package com.yzy.provider;

import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yzy
 * @version 1.0
 * @description 服务提供类
 * @date 2023/8/22 16:53
 */
public class ServiceProviderImpl implements ServiceProvider {
    public static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    public static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    public static final Set<String> registeredService = ConcurrentHashMap.newKeySet();


    /**
     * 添加服务
     *
     * @param service
     * @param serviceName
     * @param <T>
     */
    @Override
    public <T> void addServiceProvider(T service, String serviceName) {
        if (registeredService.contains(serviceName)) return;
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
    }

    /**
     * 根据服务名获取服务实例对象
     *
     * @param serviceName
     * @return
     */
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null) {
            logger.error("服务未找到");
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

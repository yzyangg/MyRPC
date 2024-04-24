package com.yzy.trans;

import com.yzy.annotation.Service;
import com.yzy.annotation.ServiceScan;
import com.yzy.provider.ServiceProvider;
import com.yzy.registry.ServiceRegistry;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import com.yzy.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/23 10:42
 */
public abstract class AbstractRpcServer implements RpcServer {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String host;
    protected int port;
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;

    /**
     * 扫描服务
     */
    public void scanService() {
        String stackTrace = ReflectUtil.getStackTrace();
        Class<?> startClass;
        try {
            startClass = Class.forName(stackTrace);
            if (!startClass.isAnnotationPresent(ServiceScan.class)) {
                logger.error("启动类缺少@ServiceScan注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.info("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }


        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        if ("".equals(basePackage)) {
            basePackage = stackTrace.substring(0, stackTrace.lastIndexOf("."));
        }
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);

        // 注册服务，给服务名赋值
        for (Class<?> clazz : classSet) {
            if (clazz.isAnnotationPresent(Service.class)) {
                String serviceName = clazz.getAnnotation(Service.class).name();
                Object obj;
                try {
                    // 找到服务 创建对象
                    obj = clazz.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建" + clazz + "时有错误发生");
                    throw new RuntimeException(e);
                }
                if ("".equals(serviceName)) {
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (Class<?> oneInterface : interfaces) {
                        publishService(obj, oneInterface.getCanonicalName());
                    }
                } else {
                    publishService(obj, serviceName);
                }
            }
        }

    }

    @Override
    public <T> void publishService(T service, String serviceName) {
        logger.info("发布服务：{} {}", serviceName, service.getClass().getCanonicalName());
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}

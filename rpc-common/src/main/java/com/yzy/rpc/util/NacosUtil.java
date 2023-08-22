package com.yzy.rpc.util;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yzy
 * @version 1.0
 * @description nacos连接工具
 * @date 2023/8/22 11:04
 */
public class NacosUtil {
    private static final Logger logger = LoggerFactory.getLogger(NacosUtil.class);

    private static final NamingService namingService;

    private static final Set<String> serviceNames = new HashSet<>();

    /**
     * 服务地址
     */
    private static InetSocketAddress address;

    private static final String SERVER_ADDR = "localhost:8848";

    static {
        namingService = getNacosNamingService();
    }

    /**
     * 获得服务
     *
     * @return
     */
    private static NamingService getNacosNamingService() {
        try {
            return NamingFactory.createNamingService(SERVER_ADDR);
        } catch (NacosException e) {
            logger.error("连接到Nacos时有错误发生: ", e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    /**
     * 注册服务
     *
     * @param serviceName
     * @param address
     * @throws NacosException
     */
    public static void registerService(String serviceName, InetSocketAddress address) throws NacosException {
        namingService.registerInstance(serviceName, address.getHostName(), address.getPort());
        NacosUtil.address = address;
        serviceNames.add(serviceName);
    }

    /**
     * 获得服务下所有实例
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 获得服务
     */
    public static void clearRegistry() {
        if (!serviceNames.isEmpty() && address != null) {
            for (String serviceName : serviceNames) {
                try {
                    namingService.deregisterInstance(serviceName, address.getHostName(), address.getPort());
                } catch (NacosException e) {
                    logger.error("注销服务 {} 失败", serviceName, e);
                }
            }
        }
    }

}


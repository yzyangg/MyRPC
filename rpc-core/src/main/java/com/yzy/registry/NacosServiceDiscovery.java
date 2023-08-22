package com.yzy.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.yzy.loadbalancer.LoadBalancer;
import com.yzy.loadbalancer.RandomLoadBalancer;
import com.yzy.rpc.exception.RpcException;
import com.yzy.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author yzy
 * @version 1.0
 * @description nacos服务发现
 * @date 2023/8/22 17:14
 */
public class NacosServiceDiscovery implements ServiceDiscovery {
    public static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    public static LoadBalancer loadBalancer = new RandomLoadBalancer();

    public NacosServiceDiscovery(LoadBalancer loadBalancer) {
        NacosServiceDiscovery.loadBalancer = loadBalancer;
    }


    /**
     * 从nacos中获取服务
     *
     * @param serviceName 服务名
     * @return
     */
    @Override
    public InetSocketAddress lookUpService(String serviceName) {
        try {
            List<Instance> allInstance = NacosUtil.getAllInstance(serviceName);
            if (allInstance.size() == 0) {
                logger.error("找不到对应的服务: " + serviceName);
                throw new RuntimeException("找不到对应的服务: " + serviceName);
            }
            Instance instance = loadBalancer.select(allInstance);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务时有错误发生:", e);
            throw new RpcException("获取服务时有错误发生:", e);
        }
    }
}

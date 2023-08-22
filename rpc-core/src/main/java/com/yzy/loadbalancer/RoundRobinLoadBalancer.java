package com.yzy.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author yzy
 * @version 1.0
 * @description 轮询负载均衡器
 * @date 2023/8/22 17:02
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
    private static int index = 0;

    @Override
    public Instance select(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}

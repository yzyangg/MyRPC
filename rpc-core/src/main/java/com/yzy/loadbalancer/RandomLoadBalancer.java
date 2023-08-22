package com.yzy.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author yzy
 * @version 1.0
 * @description 随机负载均衡器
 * @date 2023/8/22 17:01
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public Instance select(List<Instance> instances) {
        return instances.get(new java.util.Random().nextInt(instances.size()));
    }
}

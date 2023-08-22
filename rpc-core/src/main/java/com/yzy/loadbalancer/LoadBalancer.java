package com.yzy.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author yzy
 * @version 1.0
 * @description 负载均衡器
 * @date 2023/8/22 17:00
 */
public interface LoadBalancer {
    /**
     * 从服务列表中选择一个服务
     *
     * @param instances
     * @return
     */
    Instance select(List<Instance> instances);
}

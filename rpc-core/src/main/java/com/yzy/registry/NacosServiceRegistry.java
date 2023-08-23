package com.yzy.registry;

import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import com.yzy.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author yzy
 * @version 1.0
 * @description nacos服务注册
 * @date 2023/8/22 17:17
 */
public class NacosServiceRegistry implements ServiceRegistry {
    public static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);

    /**
     * 注册服务
     *
     * @param serviceName       服务名
     * @param inetSocketAddress 服务地址
     */
    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (Exception e) {
            logger.error("注册服务时有错误发生:", e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
}

package com.yzy.rpc.util;

import com.yzy.rpc.entity.RpcRequest;
import com.yzy.rpc.entity.RpcResponse;
import com.yzy.rpc.enumeration.ResponseCode;
import com.yzy.rpc.enumeration.RpcError;
import com.yzy.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yzy
 * @version 1.0
 * @description 检查响应和请求
 * @date 2023/8/23 9:19
 */
public class MessageChecker {
    public static final String INTERFACE_NAME = "interfaceName";
    public static final Logger logger = LoggerFactory.getLogger(MessageChecker.class);

    public static void check(RpcRequest request, RpcResponse response) {
        if (response == null) {
            logger.error("请求或者响应不能为空");
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + request.getInterfaceName());
        }
        if (!request.getRequestId().equals(response.getRequestId())) {
            logger.error("请求和响应的id不匹配");
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + request.getInterfaceName());
        }

        if (response.getStatusCode() == null || !response.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("服务调用失败,serviceName:{},RpcResponse:{}", request.getInterfaceName(), response);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + request.getInterfaceName());
        }
    }
}

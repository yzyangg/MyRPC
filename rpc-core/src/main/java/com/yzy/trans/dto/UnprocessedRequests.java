package com.yzy.trans.dto;

import com.yzy.rpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yzy
 * @version 1.0
 * @description 未处理的请求
 * @date 2023/8/22 17:39
 */
public class UnprocessedRequests {
    /**
     * 未处理的请求
     */
    public static final ConcurrentHashMap<String, CompletableFuture<RpcResponse>> UNPROCESSED_RESPONSE_FUTURE = new ConcurrentHashMap<>();

    /**
     * 添加未处理的请求
     *
     * @param requestId
     * @param future
     */
    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        UNPROCESSED_RESPONSE_FUTURE.put(requestId, future);
    }


    /**
     * 移除未处理的请求
     *
     * @param requestId
     */
    public void remove(String requestId) {
        UNPROCESSED_RESPONSE_FUTURE.remove(requestId);
    }


    /**
     * 收到消息后，将其从未处理的请求中移除，并将结果返回
     *
     * @param rpcResponse
     */
    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = UNPROCESSED_RESPONSE_FUTURE.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
package com.yzy.rpc.exception;

/**
 * 序列化异常
 *
 * @author yzy
 */
public class SerializeException extends RuntimeException {
    public SerializeException(String msg) {
        super(msg);
    }
}

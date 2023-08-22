package com.yzy.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yzy
 * @version 1.0
 * @description 序列化器代码
 * @date 2023/8/22 10:05
 */
@Getter
@AllArgsConstructor
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2),
    PROTOBUF(3);

    private final int code;
}

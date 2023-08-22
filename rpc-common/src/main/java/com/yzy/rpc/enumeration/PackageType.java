package com.yzy.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author yzy
 * @version 1.0
 * @description 网络包数据类型
 * @date 2023/8/22 10:04
 */
@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACK(0),

    RESPONSE_PACK(1);

    private final int code;
}

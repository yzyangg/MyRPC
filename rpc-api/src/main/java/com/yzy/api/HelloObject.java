package com.yzy.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzy
 * @version 1.0
 * @description TODO
 * @date 2023/8/22 9:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloObject {
    private Integer id;
    private String message;
}

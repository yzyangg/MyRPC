package com.yzy.server;

import com.yzy.annotation.Service;
import com.yzy.api.HelloObject;
import com.yzy.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yzy
 * @version 1.0
 * @description helloserviceimpl
 * @date 2023/8/23 11:29
 */
@Service
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject message) {
        logger.info("接收到消息：{}", message.getMessage());
        return "RPC调用成功!";
    }
}

# README

# My-RPC

My-RPC是一款基于 Nacos 实现的 RPC 框架。
通过Netty实现了网络传输，并且实现了多种序列化与负载均衡算法。

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://github.com/hhyo/archery/blob/master/LICENSE)

## 架构

系统架构

消费者调用提供者的方式取决于消费者的客户端选择，如选用原生 Socket 则该步调用使用 BIO，如选用 Netty 方式则该步调用使用
NIO。如该调用有返回值，则提供者向消费者发送返回值的方式同理。

## 特性

- 实现了基于 Netty的网络传输方式
- 实现了四种序列化算法，Json 方式、Kryo 算法、Hessian 算法与 Google Protobuf 方式（默认采用 Kryo方式序列化）
- 实现了两种负载均衡算法：随机算法与轮转算法
- 使用 Nacos 作为注册中心，管理服务提供者信息
- 消费端如采用 Netty 方式，通过ChannleProvider提供Channel，复用Channel
- 如消费端和提供者都采用 Netty 方式，会采用 Netty 的心跳机制，保证连接
- 接口抽象良好，模块耦合度低、序列化器、负载均衡算法可配置
- 实现自定义的通信协议
- 服务提供侧自动注册服务

## 架构图

![img_3.png](img_3.png)

## 目录结构图

```
My-RPC
├─rpc-api
│  ├─src
│     ├─main
│        ├─java
│           └─com
│               └─yzy
│                   └─api
├─rpc-common
│  ├─src
│     ├─main
│        ├─java
│           └─com
│               └─yzy
│                   └─rpc
│                       ├─entity
│                       ├─enumeration
│                       ├─exception
│                       ├─factory
│                       └─util
├─rpc-core
│  ├─src
│     ├─main
│        ├─java
│          └─com
│              └─yzy
│                  ├─annotation
│                  ├─codec
│                  ├─handler
│                  ├─hook
│                  ├─loadbalancer
│                  ├─provider
│                  ├─registry
│                  ├─serializer
│                  └─trans
│                      ├─client
│                      ├─dto
│                      └─server
└─rpc-test
    ├─src
       ├─main
          ├─java
             └─com
                 └─yzy
                     ├─client
                     ├─server
                     └─service

```

## 项目模块概览

- **roc-api** —— 通用接口
- **rpc-common** —— 实体对象，异常处理，工具类等
- **rpc-core** —— 框架的核心实现
- **rpc-test** —— 测试用服务端及消费端

## 传输协议

远程参数采用自定义协议如下

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

| 字段 | 解释 |
| --- | --- |
| Magic Number | 魔数，表识一个协议包，0xCAFEBABE |
| Package Type | 请求包类型，标明这是一个调用请求还是调用响应 |
| Serializer Type | 序列化器类型，标明这个包的数据的序列化方式 |
| Data Length | 数据字节的长度 |
| Data Bytes | 传输的对象，通常是一个RpcRequest或RpcClient对象，取决于Package Type字段，对象的序列化方式取决于Serializer Type字段。 |

## 使用

### 定义调用接口

```java
package com.yzy.api;

/**
 * @author yzy
 * @version 1.0
 * @description helloservice接口
 * @date 2023/8/22 9:39
 */
public interface HelloService {
    String hello(HelloObject message);
}
```

### 在服务提供侧实现该接口

```java
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
        return "这是Impl1方法";
    }
}
```

### 编写服务提供者

```java
package com.yzy.server;

import com.yzy.annotation.ServiceScan;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.server.NettyServer;

/**
 * @author yzy
 * @version 1.0
 * @description nettytestserver
 * @date 2023/8/23 11:32
 */
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer("localhost", 9999, CommonSerializer.DEFAULT_SERIALIZER);
        nettyServer.start();
    }
}
```

这里选用 Netty 传输方式，并且指定序列化方式为默认***Kryo*** 方式。

### 在服务消费侧远程调用

```java
package com.yzy.client;

import com.yzy.api.HelloObject;
import com.yzy.api.HelloService;
import com.yzy.serializer.CommonSerializer;
import com.yzy.trans.RpcClient;
import com.yzy.trans.RpcClientProxy;
import com.yzy.trans.client.NettyClient;

/**
 * @author yzy
 * @version 1.0
 * @description nettyTestClient
 * @date 2023/8/23 11:15
 */
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient(CommonSerializer.DEFAULT_SERIALIZER);
        // 代理类
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        // 通过代理类获得服务
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject message = new HelloObject(12, "This is a message");
        // 通过代理发送请求
        String res = helloService.hello(message);
        System.out.println(res);
    }
}
```

客户端选用 Netty 的传输方式，序列化方式采用 ***Kryo*** 方式，负载均衡策略指定为默认轮转。

### 启动

启动Nacos 运行在本地 `8848` 端口。

首先启动服务提供者，再启动消费者。

启动后消费者端会看到 _**RPC调用成功!**_
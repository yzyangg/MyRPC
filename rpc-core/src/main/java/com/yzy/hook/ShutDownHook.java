package com.yzy.hook;

import com.yzy.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yzy
 * @version 1.0
 * @description 关闭后清除所有服务
 * @date 2023/8/22 17:04
 */
public class ShutDownHook {
    public static final Logger logger = LoggerFactory.getLogger(ShutDownHook.class);
    private static final ShutDownHook shutDownHook = new ShutDownHook();

    public static ShutDownHook getShutDownHook() {
        return shutDownHook;
    }

    /**
     * 关闭后清除所有服务
     */
    public void addClearAllHock() {
        logger.info("关闭后清除所有服务");
        // 向JVM注册一个关闭的钩子线程，当JVM关闭的时候，会启动一个线程，执行系统中已经设置的所有通过方法addShutdownHook添加的钩子线程
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            NacosUtil.clearRegistry();
            logger.info("关闭后清除所有服务");
        }));

    }
}

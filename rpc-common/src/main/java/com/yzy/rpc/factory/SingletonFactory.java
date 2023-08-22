package com.yzy.rpc.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yzy
 * @version 1.0
 * @description 单例工厂
 * @date 2023/8/22 10:08
 */
public class SingletonFactory {
    private static final Map<Class, Object> objectMap = new ConcurrentHashMap<>();

    private SingletonFactory() {
    }

    /**
     * 双检索单例模式
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<T> clazz) {
        Object instance = objectMap.get(clazz);
        synchronized (clazz) {
            if (instance == null) {
                try {
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return clazz.cast(instance);
    }
}

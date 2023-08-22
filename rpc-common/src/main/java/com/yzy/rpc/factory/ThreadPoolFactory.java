package com.yzy.rpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author yzy
 * @version 1.0
 * @description 线程池工厂
 * @date 2023/8/22 10:07
 */
public class ThreadPoolFactory {
    /**
     * 线程池参数
     */
    private static final int CORE_POOL_SIZE = 10;
    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    /**
     * 空闲线程存活时间
     */
    private static final int KEEP_ALIVE_TIME = 1;
    /**
     * 阻塞队列容量
     */
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    /**
     * 线程池
     */
    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static final Map<String, ExecutorService> threadPollsMap = new ConcurrentHashMap<>();

    private ThreadPoolFactory() {
    }


    /**
     * 创建默认线程池，非守护线程
     *
     * @param threadNamePrefix
     * @return
     */
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    /**
     * 创建默认线程池可指定守护或非守护
     *
     * @param threadNamePrefix
     * @param daemon
     * @return
     */
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, daemon));
        // 如果线程池已经被关闭或者终止了，就移除线程池并重新创建一个
        if (pool != null && (pool.isShutdown() || pool.isTerminated())) {
            threadPollsMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPollsMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    /**
     * 关闭所有线程池
     */
    public static void shutDownAll() {
        logger.info("关闭所有线程池...");
        threadPollsMap.entrySet().parallelStream().forEach(one -> {
            ExecutorService service = one.getValue();
            //开始关闭线程池
            service.shutdown();
            //判断线程池是否已经关闭
            logger.info("关闭线程池[{}] [{}]", one.getKey(), service.isTerminated());
            try {
                //等待线程池关闭的时间
                service.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("关闭线程池失败！");
                //如果等待超时或者线程池出现中断异常，则强制关闭线程池
                service.shutdownNow();
            }
        });
    }

    /**
     * 创建线程池
     *
     * @param threadNamePrefix
     * @param daemon
     * @return
     */
    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE_SIZE,
                KEEP_ALIVE_TIME,
                java.util.concurrent.TimeUnit.MINUTES,
                queue,
                threadFactory);
    }

    /**
     * 创建线程工厂
     *
     * @param threadNamePrefix
     * @param daemon
     * @return
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (daemon != null) {
            return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
        } else {
            return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
        }
    }
}

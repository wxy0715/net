package com.net.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工具类
 * @author wxy
 */
@Slf4j
public class ThreadPoolFactory {
    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE * 10;
    /**
     * 超时时间
     */
    private final static long KEEP_ALIVE_TIME = 60L;


    /**
     * 线程池
     */
    private static final ThreadPoolExecutor THREADPOOLEXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(), new CustomThreadFactory("net-thread-"), new ThreadPoolExecutor.DiscardPolicy());

    /**
     * 创建一个定时任务线程池
     */
    private static final ScheduledThreadPoolExecutor SCHEDULED_THREAD_POOL_EXECUTOR = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("net-scheduled-"));

    /**
     * 执行定时任务
     * @param runnable 任务
     * @param initialDelay 初始延迟时间
     * @param period 间隔
     * @param unit 单位
     */
    public static void scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        SCHEDULED_THREAD_POOL_EXECUTOR.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * 执行提交的任务
     * @param runnable 任务
     */
    public static void execute(Runnable runnable) {
        THREADPOOLEXECUTOR.execute(runnable);
    }

    /**
     * 执行提交的任务带返回值
     * @param callable 任务
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return THREADPOOLEXECUTOR.submit(callable);
    }

    /**
     * 设置核心线程数
     *
     * @param corePoolSize 任务
     */
    public static void setCorePoolSize(int corePoolSize) {
        THREADPOOLEXECUTOR.setCorePoolSize(corePoolSize);
    }

    /**
     * 最大连接数
     *
     * @return int
     */
    public static int getMaxActive() {
        return (MAXIMUM_POOL_SIZE / 2) - CORE_POOL_SIZE;
    }

    /**
     * 活动线程数
     */
    public static int getActiveCount() {
        return THREADPOOLEXECUTOR.getActiveCount();
    }

    /**
     * 最大线程数
     */
    public static int getMaximumPoolSize() {
        return THREADPOOLEXECUTOR.getMaximumPoolSize();
    }

    /**
     * 核心线程数
     */
    public static int getCorePoolSize() {
        return THREADPOOLEXECUTOR.getCorePoolSize();
    }

    public static void shutdown() {
        if (!THREADPOOLEXECUTOR.isShutdown()) {
            THREADPOOLEXECUTOR.shutdown();
        }
        if (!SCHEDULED_THREAD_POOL_EXECUTOR.isShutdown()) {
            SCHEDULED_THREAD_POOL_EXECUTOR.shutdown();
        }
    }




    /**
     * 线程工厂
     */
    static class CustomThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private CustomThreadFactory(String namePrefix) {
            // final是不可变的,所有在建立一个临时变量,解决catch重新复制
            ThreadGroup systemThreadGroup = null;
            SecurityManager s = System.getSecurityManager();
            if (s != null) {
                try {
                    systemThreadGroup = s.getThreadGroup();
                } catch (SecurityException se) {
                    log.error("Failed to get thread group from SecurityManager", se);
                    systemThreadGroup = Thread.currentThread().getThreadGroup();
                }
            } else {
                systemThreadGroup = Thread.currentThread().getThreadGroup();
            }
            this.namePrefix = namePrefix;
            group = systemThreadGroup;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            // 把线程改为非守护线程
            t.setDaemon(false);
            t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }


}



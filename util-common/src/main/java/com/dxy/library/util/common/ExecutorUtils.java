package com.dxy.library.util.common;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池工具类
 * @author duanxinyuan
 * 2017/8/14 18:30
 */
public class ExecutorUtils {

    private ExecutorUtils() {
    }

    /**
     * 生成定时线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     */
    public static ScheduledExecutorService getScheduledExecutorService(String name, int corePollSize) {
        return getScheduledExecutorService(name, corePollSize, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 生成定时线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param rejectedExecutionHandler 拒绝策略
     */
    public static ScheduledExecutorService getScheduledExecutorService(String name, int corePollSize, RejectedExecutionHandler rejectedExecutionHandler) {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern(name + "-schedule-pool-%d").daemon(true).build();
        return new ScheduledThreadPoolExecutor(corePollSize, basicThreadFactory, rejectedExecutionHandler);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     */
    public static ExecutorService getExecutorService(String name, int corePollSize) {
        return getExecutorService(name, corePollSize, corePollSize, 60, 1024);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param queueSize 队列长度
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int queueSize) {
        return getExecutorService(name, corePollSize, maxmumPoolSize, 60, queueSize);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param workQueue 工作队列
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, BlockingQueue<Runnable> workQueue) {
        return getExecutorService(name, corePollSize, maxmumPoolSize, 60, TimeUnit.SECONDS, workQueue);
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param queueSize 队列长度
     * @param keepAliveSeconds 空闲线程存活时间
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int keepAliveSeconds, int queueSize) {
        if (queueSize <= 0) {
            queueSize = corePollSize;
        }
        return getExecutorService(name, corePollSize, maxmumPoolSize, keepAliveSeconds, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize));
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param workQueue 工作队列
     * @param keepAliveTime 空闲线程存活时间
     * @param timeUnit 空闲线程存活时长的单位
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue) {
        return getExecutorService(name, corePollSize, maxmumPoolSize, keepAliveTime, timeUnit, workQueue, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * 生成缓存线程池
     * @param name 名称
     * @param corePollSize 核心线程数
     * @param maxmumPoolSize 最大线程数
     * @param workQueue 工作队列
     * @param keepAliveTime 空闲线程存活时间
     * @param timeUnit 空闲线程存活时长的单位
     * @param rejectedExecutionHandler 拒绝策略
     */
    public static ExecutorService getExecutorService(String name, int corePollSize, int maxmumPoolSize, int keepAliveTime, TimeUnit timeUnit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler rejectedExecutionHandler) {
        BasicThreadFactory basicThreadFactory = new BasicThreadFactory.Builder().namingPattern(name + "-pool-%d").daemon(true).build();
        return new ThreadPoolExecutor(corePollSize, maxmumPoolSize, keepAliveTime, timeUnit, workQueue, basicThreadFactory, rejectedExecutionHandler);
    }
}


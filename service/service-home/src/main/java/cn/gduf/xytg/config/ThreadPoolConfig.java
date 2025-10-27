package cn.gduf.xytg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author LuoXuanwei
 * @version 1.0
 * @description 线程池配置类
 * @date 2025/10/27 20:40
 */
@Configuration
public class ThreadPoolConfig {
    /**
     * 创建并配置线程池执行器Bean
     *
     * @return ThreadPoolExecutor 线程池执行器实例
     *
     * 线程池配置说明：
     * - 核心线程数：2
     * - 最大线程数：5
     * - 空闲线程存活时间：2秒
     * - 工作队列：容量为3的数组阻塞队列
     * - 线程工厂：使用默认线程工厂
     * - 拒绝策略：直接抛出异常的中止策略
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                2,
                5,
                2,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        return threadPoolExecutor;
    }
}


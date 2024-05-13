package com.zerobase.dividend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

        // cpu 코어의 갯수
        int n = Runtime.getRuntime().availableProcessors();
        threadPool.setPoolSize(n);
        threadPool.initialize();

        // 스레드 풀 사용
        taskRegistrar.setTaskScheduler(threadPool);
    }
}

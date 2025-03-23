package com.example.bid_api.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Data
public class ThreadPoolConfig {
    private ThreadPoolTaskExecutor mailThreadPool;

    public ThreadPoolConfig() {
        this.mailThreadPool = setThreadPool(10, 30, "Mail-Pool-");
    }

    public static ThreadPoolTaskExecutor setThreadPool(int corePoolSize, int maximumPoolSize, String threadNamePrefix) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maximumPoolSize);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}

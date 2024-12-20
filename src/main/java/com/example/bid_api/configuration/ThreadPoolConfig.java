//package com.example.bid_api.configuration;
//
//import lombok.Data;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.ThreadPoolExecutor;
//
//@Configuration
//@Data
//public class ThreadPoolConfig {
//    private ThreadPoolTaskExecutor messageResponseThreadPool;
//    private ThreadPoolTaskExecutor messageConsumerThreadPool;
//    private ThreadPoolTaskExecutor messageShareConsumerThreadPool;
//    private ThreadPoolTaskExecutor commentConsumerThreadPool;
//
//    public ThreadPoolConfig() {
//        this.messageResponseThreadPool = setThreadPool(5, 10, "Message-Response-Pool-");
//        this.messageConsumerThreadPool = setThreadPool(5, 10, "Message-Consumer-Pool-");
//        this.messageShareConsumerThreadPool = setThreadPool(5, 10, "Message-Share-Consumer-Pool-");
//        this.commentConsumerThreadPool = setThreadPool(5, 10, "Comment-Consumer-Pool-");
//    }
//
//    public static ThreadPoolTaskExecutor setThreadPool(int corePoolSize, int maximumPoolSize, String threadNamePrefix) {
//        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
//        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
//        threadPoolTaskExecutor.setMaxPoolSize(maximumPoolSize);
//        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
//        threadPoolTaskExecutor.initialize();
//        return threadPoolTaskExecutor;
//    }
//}

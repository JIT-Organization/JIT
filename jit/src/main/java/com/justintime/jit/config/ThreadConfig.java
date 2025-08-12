package com.justintime.jit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class ThreadConfig {

    // 1 thread → STOMP heart-beats only
    @Bean("heartBeatTaskScheduler")
    public ThreadPoolTaskScheduler heartBeatTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    // 8 threads → frame routing & user sends
    @Bean("wsBrokerScheduler")
    public ThreadPoolTaskScheduler wsBrokerScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ws-broker-");
        scheduler.setPoolSize(8);
        scheduler.initialize();
        return scheduler;
    }
}

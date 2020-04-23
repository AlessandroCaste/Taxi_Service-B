package com.taxi.sb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class.getName());

    @Bean("threadPoolTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setMaxPoolSize(16);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix("GraphManager-");
        return executor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}

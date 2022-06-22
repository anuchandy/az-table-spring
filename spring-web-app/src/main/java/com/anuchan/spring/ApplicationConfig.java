package com.anuchan.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.Assert;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class ApplicationConfig {
    @Bean
    public TableServiceClient tableServiceClient(@Value("${azure.connString}") String connString) {
        Assert.notNull(connString, "azure storage connString must be set");
        return new TableServiceClientBuilder()
                .connectionString(connString)
                .buildClient();
    }

    @Bean(name = "threadPoolTaskExecutor")
    public Executor apiCallExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(10 * Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(100000);
        executor.setThreadNamePrefix("apiCallExecutor::");
        executor.initialize();
        return executor;
    }
}

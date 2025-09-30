package anxiety.detector.api.service.framework.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${async.mail.core-pool:4}")        
    private int corePool;

    @Value("${async.mail.max-pool:8}")         
    private int maxPool;

    @Value("${async.mail.queue-capacity:100}") 
    private int queueCapacity;

    @Bean("mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setThreadNamePrefix("mail-");
        ex.setCorePoolSize(corePool);
        ex.setMaxPoolSize(maxPool);
        ex.setQueueCapacity(queueCapacity);
        ex.initialize();
        return ex;
    }
}

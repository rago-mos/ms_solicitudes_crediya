package co.com.crediya.sqs.sender.config;

import co.com.crediya.model.loantype.enums.SqsQueueType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "adapter.sqs")
@Data
public class SqsQueuesProperties {

    private final Map<String, QueueConfig> queues = new HashMap<>();

    @Data
    public static class QueueConfig {
        private String queueUrl;
    }

    public String getQueueUrl(SqsQueueType type) {
        return queues.get(type.name().toLowerCase()).getQueueUrl();
    }
}


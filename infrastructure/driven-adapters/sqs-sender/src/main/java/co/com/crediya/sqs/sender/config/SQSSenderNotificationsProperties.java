package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs.notifications")
public record SQSSenderNotificationsProperties(
     String region,
     String queueUrl,
     String endpoint){
}

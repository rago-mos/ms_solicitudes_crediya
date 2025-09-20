package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs.general")
public record SQSSenderProperties(
     String region,
     String endpoint){
}

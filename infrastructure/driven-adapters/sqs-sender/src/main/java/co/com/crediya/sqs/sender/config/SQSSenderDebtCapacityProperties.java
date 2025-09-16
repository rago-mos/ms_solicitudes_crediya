package co.com.crediya.sqs.sender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "adapter.sqs.capacity")
public record SQSSenderDebtCapacityProperties(
     String region,
     String queueUrl,
     String endpoint){
}

package co.com.crediya.sqs.sender;

import co.com.crediya.model.application.gateways.SqsMessageGateway;
import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.model.loantype.enums.SqsQueueType;
import co.com.crediya.sqs.sender.config.SqsQueuesProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static co.com.crediya.model.utils.Constant.ERROR_JSON_PROCESSING;
import static co.com.crediya.model.utils.Constant.LOG_DEBUG_SQS_SEND;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsMessageGateway {

    private final SqsQueuesProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public <T> Mono<String> send(T message, SqsQueueType type) {

        return Mono.fromCallable(() -> objectMapper.writeValueAsString(message))
                .onErrorMap(JsonProcessingException.class,
                        e -> new SqsMessageException(ERROR_JSON_PROCESSING))
                .flatMap(messageBody -> Mono.just(
                        SendMessageRequest.builder()
                                .queueUrl(properties.getQueueUrl(type))
                                .messageBody(messageBody)
                                .build())
                )
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info(LOG_DEBUG_SQS_SEND, response.messageId()))
                .map(SendMessageResponse::messageId);
    }
}

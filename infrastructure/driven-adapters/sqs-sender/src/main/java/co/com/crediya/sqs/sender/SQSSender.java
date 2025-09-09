package co.com.crediya.sqs.sender;

import co.com.crediya.model.application.dto.UpdateApplicationView;
import co.com.crediya.model.application.gateways.SqsMessageRepository;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import static co.com.crediya.model.utils.Constant.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements SqsMessageRepository {

    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> send(UpdateApplicationView message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);

            return Mono.fromCallable(() -> buildRequest(messageBody))
                    .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                    .doOnNext(response -> log.debug(LOG_DEBUG_SQS_SEND, response.messageId()))
                    .map(SendMessageResponse::messageId);
        }
        catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException(ERROR_JSON_PROCESSING));
        }
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}

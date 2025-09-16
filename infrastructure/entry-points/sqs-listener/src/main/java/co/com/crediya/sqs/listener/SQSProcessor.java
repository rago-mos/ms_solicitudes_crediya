package co.com.crediya.sqs.listener;

import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.sqs.listener.dto.CalculateCapacityResponse;
import co.com.crediya.sqs.listener.mapper.SqsMapper;
import co.com.crediya.usecase.loanapplication.IUpdateApplicationUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

import static co.com.crediya.model.utils.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;
    private final IUpdateApplicationUseCase updateApplicationUseCase;
    private final SqsMapper sqsMapper;

    @Override
    public Mono<Void> apply(Message message) {

        log.info(LOG_INFO_SQS_RECEIVED_UPDATED_STATUS, message.messageId());

        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), CalculateCapacityResponse.class))
                .onErrorMap(JsonProcessingException.class,
                        e -> new SqsMessageException(ERROR_JSON_PROCESSING_DES))
                .flatMap(response ->
                        updateApplicationUseCase.updateApplicationCalculate(sqsMapper.toNotificationData(response))
                );
    }
}

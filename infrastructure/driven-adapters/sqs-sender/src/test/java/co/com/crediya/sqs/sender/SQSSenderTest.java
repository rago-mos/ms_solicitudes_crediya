package co.com.crediya.sqs.sender;

import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.model.loantype.enums.SqsQueueType;
import co.com.crediya.sqs.sender.config.SqsQueuesProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SqsQueuesProperties properties;

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    private SQSSender sender;

    @BeforeEach
    void setUp() {
        sender = new SQSSender(properties, client, objectMapper);
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {

        DummyMessage dummy = new DummyMessage("test");
        String serialized = "{\"value\":\"test\"}";
        String queueUrl = "https://sqs.aws/reports";

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-123")
                .build();

        when(objectMapper.writeValueAsString(dummy)).thenReturn(serialized);
        when(properties.getQueueUrl(SqsQueueType.REPORTS)).thenReturn(queueUrl);
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        Mono<String> result = sender.send(dummy, SqsQueueType.REPORTS);

        StepVerifier.create(result)
                .expectNext("msg-123")
                .verifyComplete();

        verify(objectMapper).writeValueAsString(dummy);
        verify(properties).getQueueUrl(SqsQueueType.REPORTS);
    }

    @Test
    void shouldThrowSqsMessageExceptionOnSerializationError() throws Exception {

        DummyMessage dummy = new DummyMessage("fail");
        when(objectMapper.writeValueAsString(dummy))
                .thenThrow(new JsonProcessingException("error") {});

        Mono<String> result = sender.send(dummy, SqsQueueType.REPORTS);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof SqsMessageException &&
                        e.getMessage().equals("Error serializing message"))
                .verify();

        verify(objectMapper).writeValueAsString(dummy);
        verifyNoInteractions(client);
    }

    record DummyMessage(String value) {}
}

package co.com.crediya.sqs.sender;

import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.sqs.sender.config.SQSSenderDebtCapacityProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderDebtCapacityTest {

    @Mock
    private SQSSenderDebtCapacityProperties properties;

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    private SQSSenderDebtCapacity sender;

    @BeforeEach
    void setUp() {
        sender = new SQSSenderDebtCapacity(properties, client, objectMapper);
    }

    @Test
    void shouldSendMessageSuccessfully() throws Exception {

        TestPayload payload = new TestPayload("Rubén", 1234L);
        String json = "{\"name\":\"Rubén\",\"id\":1234}";
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/debt-capacity";

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-001")
                .build();

        CompletableFuture<SendMessageResponse> future = CompletableFuture.completedFuture(response);

        when(objectMapper.writeValueAsString(payload)).thenReturn(json);
        when(properties.queueUrl()).thenReturn(queueUrl);
        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        StepVerifier.create(sender.send(payload))
                .expectNext("msg-001")
                .verifyComplete();

        verify(objectMapper).writeValueAsString(payload);
        verify(client).sendMessage(argThat((SendMessageRequest req) ->
                req.queueUrl().equals(queueUrl) &&
                        req.messageBody().equals(json)
        ));
    }

    @Test
    void shouldReturnErrorWhenSerializationFails() throws Exception {

        TestPayload payload = new TestPayload("Rubén", 1234L);

        when(objectMapper.writeValueAsString(payload))
                .thenThrow(new JsonProcessingException("Boom") {});

        StepVerifier.create(sender.send(payload))
                .expectErrorMatches(error ->
                        error instanceof SqsMessageException &&
                                error.getMessage().equals("Error serializing message")
                )
                .verify();

        verify(objectMapper).writeValueAsString(payload);
        verifyNoInteractions(client);
    }

    record TestPayload(String name, Long id) {}
}

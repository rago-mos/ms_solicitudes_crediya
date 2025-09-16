package co.com.crediya.sqs.sender;

import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.sqs.sender.config.SQSSenderNotificationsProperties;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderNotificationsTest {

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    private SQSSenderNotifications sender;

    private SQSSenderNotificationsProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SQSSenderNotificationsProperties("us-east-1", "https://sqs.us-east-1.amazonaws.com/queue", null);
        sender = new SQSSenderNotifications(properties, client, objectMapper);
    }

    @Test
    void shouldSendMessageSuccessfully() throws JsonProcessingException {

        NotificationData message = NotificationData.builder()
                .idApplication(1L)
                .identityDocument("123456789")
                .fullName("Rubén Tester")
                .email("ruben@example.com")
                .statusName("Approved")
                .loanTypeName("Personal")
                .build();

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-123")
                .build();

        CompletableFuture<SendMessageResponse> future = CompletableFuture.completedFuture(response);
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"mocked\":\"json\"}");
        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        StepVerifier.create(sender.send(message))
                .expectNext("msg-123")
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenSerializationFails() throws JsonProcessingException {

        NotificationData message = NotificationData.builder()
                .idApplication(1L)
                .identityDocument("123456789")
                .fullName("Rubén Tester")
                .email("ruben@example.com")
                .statusName("Approved")
                .loanTypeName("Personal")
                .build();

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {});

        StepVerifier.create(sender.send(message))
                .expectErrorMatches(error ->
                        error instanceof SqsMessageException &&
                                error.getMessage().equals("Error serializing message")
                )
                .verify();
    }

}

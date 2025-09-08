package co.com.crediya.sqs.sender;

import co.com.crediya.model.application.dto.UpdateApplicationView;
import co.com.crediya.sqs.sender.config.SQSSenderProperties;
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

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SqsAsyncClient client;

    @Mock
    private ObjectMapper objectMapper;

    private SQSSender sender;

    private SQSSenderProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SQSSenderProperties("us-east-1", "https://sqs.us-east-1.amazonaws.com/queue", null);
        sender = new SQSSender(properties, client, objectMapper);
    }

    @Test
    void shouldSendMessageSuccessfully() {

        UpdateApplicationView message = UpdateApplicationView.builder()
                .idApplication("APP001")
                .amount(BigDecimal.valueOf(1000000))
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
        when(client.sendMessage(any(SendMessageRequest.class))).thenReturn(future);

        StepVerifier.create(sender.send(message))
                .expectNext("msg-123")
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenSerializationFails() throws JsonProcessingException {

        UpdateApplicationView message = UpdateApplicationView.builder()
                .idApplication("APP001")
                .amount(BigDecimal.valueOf(1000000))
                .identityDocument("123456789")
                .fullName("Rubén Tester")
                .email("ruben@example.com")
                .statusName("Approved")
                .loanTypeName("Personal")
                .build();

        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("fail") {});

        StepVerifier.create(sender.send(message))
                .expectErrorMatches(error ->
                        error instanceof RuntimeException &&
                                error.getMessage().equals("Error serializing message")
                )
                .verify();
    }

}

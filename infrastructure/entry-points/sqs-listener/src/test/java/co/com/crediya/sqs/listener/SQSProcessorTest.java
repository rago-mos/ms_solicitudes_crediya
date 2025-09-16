package co.com.crediya.sqs.listener;

import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.exception.SqsMessageException;
import co.com.crediya.sqs.listener.dto.CalculateCapacityResponse;
import co.com.crediya.sqs.listener.mapper.SqsMapper;
import co.com.crediya.usecase.loanapplication.IUpdateApplicationUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSProcessorTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IUpdateApplicationUseCase updateApplicationUseCase;

    @Mock
    private SqsMapper sqsMapper;

    private SQSProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SQSProcessor(objectMapper, updateApplicationUseCase, sqsMapper);
    }

    @Test
    void shouldProcessValidMessageSuccessfully() throws Exception {
        // Arrange
        String json = """
            {
                "idApplication": 1001,
                "statusId": 2,
                "status": "Aprobado",
                "loanType": "Consumo",
                "plan": [],
                "name": "Rubén",
                "email": "ruben@example.com",
                "document": "123456789"
            }
        """;

        Message message = Message.builder()
                .messageId("msg-001")
                .body(json)
                .build();

        CalculateCapacityResponse response = new CalculateCapacityResponse(
                1001L, 2, "Aprobado", "Consumo", List.of(), "Rubén", "ruben@example.com", "123456789"
        );

        NotificationData notificationData = NotificationData.builder()
                .idApplication(1001L)
                .identityDocument("123456789")
                .fullName("Rubén")
                .email("ruben@example.com")
                .idStatus(2)
                .statusName("Aprobado")
                .loanTypeName("Consumo")
                .paymentPlans(List.of())
                .isValidatedAutomatic(true)
                .build();

        when(objectMapper.readValue(json, CalculateCapacityResponse.class)).thenReturn(response);
        when(sqsMapper.toNotificationData(response)).thenReturn(notificationData);
        when(updateApplicationUseCase.updateApplicationCalculate(notificationData)).thenReturn(Mono.empty());

        StepVerifier.create(processor.apply(message))
                .verifyComplete();

        verify(objectMapper).readValue(json, CalculateCapacityResponse.class);
        verify(sqsMapper).toNotificationData(response);
        verify(updateApplicationUseCase).updateApplicationCalculate(notificationData);
    }

    @Test
    void shouldReturnErrorWhenJsonIsInvalid() throws Exception {

        String invalidJson = "{ invalid json }";
        Message message = Message.builder()
                .messageId("msg-001")
                .body(invalidJson)
                .build();

        when(objectMapper.readValue(invalidJson, CalculateCapacityResponse.class))
                .thenThrow(new JsonProcessingException("Malformed") {});

        StepVerifier.create(processor.apply(message))
                .expectErrorMatches(error ->
                        error instanceof SqsMessageException &&
                                error.getMessage().equals("Error deserializing message")
                )
                .verify();

        verify(objectMapper).readValue(invalidJson, CalculateCapacityResponse.class);
        verifyNoInteractions(sqsMapper, updateApplicationUseCase);
    }
}

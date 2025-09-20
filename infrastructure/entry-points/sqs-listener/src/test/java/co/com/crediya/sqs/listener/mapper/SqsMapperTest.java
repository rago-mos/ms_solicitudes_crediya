package co.com.crediya.sqs.listener.mapper;

import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.application.dto.PaymentPlan;
import co.com.crediya.sqs.listener.dto.CalculateCapacityResponse;
import co.com.crediya.sqs.listener.dto.PlanDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SqsMapperTest {

    private SqsMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SqsMapper();
    }

    @Test
    void shouldMapFullCalculateCapacityResponseToNotificationData() {

        PlanDTO planDTO = new PlanDTO(
                6,
                new BigDecimal("5000.00"),
                new BigDecimal("500.00"),
                new BigDecimal("5500.00")
        );

        CalculateCapacityResponse response = new CalculateCapacityResponse(
                1001L,
                3,
                "Aprobado",
                "Consumo",
                List.of(planDTO),
                "Rubén Darío",
                "ruben@example.com",
                "1234567890",
                new BigDecimal("500000")
        );

        NotificationData result = mapper.toNotificationData(response);

        assertEquals(1001L, result.getIdApplication());
        assertEquals("1234567890", result.getIdentityDocument());
        assertEquals("Rubén Darío", result.getFullName());
        assertEquals("ruben@example.com", result.getEmail());
        assertEquals(3, result.getIdStatus());
        assertEquals("Aprobado", result.getStatusName());
        assertEquals("Consumo", result.getLoanTypeName());
        assertTrue(result.getIsValidatedAutomatic());

        assertNotNull(result.getPaymentPlans());
        assertEquals(1, result.getPaymentPlans().size());

        PaymentPlan plan = result.getPaymentPlans().get(0);
        assertEquals(6, plan.getMonth());
        assertEquals(new BigDecimal("5000.00"), plan.getCapital());
        assertEquals(new BigDecimal("500.00"), plan.getInterest());
        assertEquals(new BigDecimal("5500.00"), plan.getTotal());
    }

    @Test
    void shouldReturnEmptyPaymentPlansWhenPlanIsNull() {

        CalculateCapacityResponse response = new CalculateCapacityResponse(
                2002L,
                1,
                "Pendiente",
                "Hipotecario",
                null,
                "Ana María",
                "ana@example.com",
                "9876543210",
                new BigDecimal("500000")
        );

        NotificationData result = mapper.toNotificationData(response);

        assertNotNull(result.getPaymentPlans());
        assertTrue(result.getPaymentPlans().isEmpty());
    }
}
package co.com.crediya.sqs.listener.mapper;

import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.application.dto.PaymentPlan;
import co.com.crediya.sqs.listener.dto.CalculateCapacityResponse;
import co.com.crediya.sqs.listener.dto.PlanDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SqsMapper {

    public NotificationData toNotificationData(CalculateCapacityResponse response) {
        return NotificationData.builder()
                .idApplication(response.idApplication())
                .identityDocument(response.document())
                .fullName(response.name())
                .email(response.email())
                .idStatus(response.statusId())
                .statusName(response.status())
                .loanTypeName(response.loanType())
                .paymentPlans(toPaymentPlans(response.plan()))
                .isValidatedAutomatic(true)
                .build();
    }

    private List<PaymentPlan> toPaymentPlans(List<PlanDTO> plan) {

        if (plan == null) {
            return new ArrayList<>();
        }

        return plan.stream()
                .map(dto -> PaymentPlan.builder()
                        .month(dto.month())
                        .capital(dto.principal())
                        .interest(dto.interest())
                        .total(dto.total())
                        .build())
                .toList();
    }
}

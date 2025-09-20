package co.com.crediya.sqs.listener.dto;

import java.math.BigDecimal;
import java.util.List;

public record CalculateCapacityResponse(
        Long idApplication,
        Integer statusId,
        String status,
        String loanType,
        List<PlanDTO> plan,
        String name,
        String email,
        String document,
        BigDecimal amount
) {
}

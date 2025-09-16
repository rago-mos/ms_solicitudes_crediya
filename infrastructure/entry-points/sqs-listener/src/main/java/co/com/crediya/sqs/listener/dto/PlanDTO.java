package co.com.crediya.sqs.listener.dto;

import java.math.BigDecimal;

public record PlanDTO(
     Integer month,
     BigDecimal principal,
     BigDecimal interest,
     BigDecimal total
){
}

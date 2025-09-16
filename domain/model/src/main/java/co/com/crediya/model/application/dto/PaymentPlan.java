package co.com.crediya.model.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PaymentPlan {

    private Integer month;
    private BigDecimal capital;
    private BigDecimal interest;
    private BigDecimal total;

}

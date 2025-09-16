package co.com.crediya.model.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationAprovedView {

    private BigDecimal amount;
    private Integer term;
    private BigDecimal interest;
    private String loanTypeName;
}

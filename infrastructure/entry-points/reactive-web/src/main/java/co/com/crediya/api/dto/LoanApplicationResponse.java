package co.com.crediya.api.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationResponse {

    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private StateResponse state;
    private LoanTypeResponse loanType;
}

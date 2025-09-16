package co.com.crediya.model.application;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Application {

    private Long idApplication;
    private BigDecimal amount;
    private Integer term;
    private String identityDocument;
    private State state;
    private LoanType loanType;
    private LocalDate date;

}

package co.com.crediya.model.application.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationView {

    private BigDecimal amount;
    private Integer monthTerm;
    private String identityDocument;
    private BigDecimal monthAmountApprovedApplication;
    private String fullName;
    private String email;
    private String statusName;
    private BigDecimal interestRate;
    private String loanTypeName;
    private BigDecimal baseSalary;
}

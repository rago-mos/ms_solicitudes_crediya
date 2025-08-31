package co.com.crediya.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record LoanApplicationRequest(

        @NotNull(message = "The field is mandatory")
        BigDecimal amount,

        @NotNull(message = "The field is mandatory")
        Integer term,

        @Pattern(regexp = "^\\d{1,20}$", message = "The field must contain only numeric digits and must not exceed 20 characters")
        @NotNull(message = "The field is mandatory")
        String identityDocument,

        @NotNull(message = "The field is mandatory")
        Integer idLoanType
) {
}

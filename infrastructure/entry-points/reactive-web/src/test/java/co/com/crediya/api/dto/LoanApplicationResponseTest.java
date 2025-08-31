package co.com.crediya.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationResponseTest {

    @Test
    void shouldBuildLoanApplicationResponseCorrectly() {
        StateResponse state = StateResponse.builder()
                .name("Approved")
                .description("Solicitud aprobada")
                .build();

        LoanTypeResponse loanType = LoanTypeResponse.builder()
                .name("Educativo")
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("0.03"))
                .automaticValidation(true)
                .build();

        LoanApplicationResponse response = LoanApplicationResponse.builder()
                .amount(new BigDecimal("500000"))
                .term(12)
                .identityDocument("123456789")
                .state(state)
                .loanType(loanType)
                .build();

        assertEquals(new BigDecimal("500000"), response.getAmount());
        assertEquals(12, response.getTerm());
        assertEquals("123456789", response.getIdentityDocument());

        assertEquals("Approved", response.getState().getName());
        assertEquals("Solicitud aprobada", response.getState().getDescription());

        assertEquals("Educativo", response.getLoanType().getName());
        assertEquals(new BigDecimal("100000"), response.getLoanType().getMinimumAmount());
        assertEquals(new BigDecimal("2000000"), response.getLoanType().getMaximumAmount());
        assertEquals(new BigDecimal("0.03"), response.getLoanType().getInterestRate());
        assertTrue(response.getLoanType().getAutomaticValidation());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        LoanApplicationResponse response = new LoanApplicationResponse();

        response.setAmount(new BigDecimal("750000"));
        response.setTerm(24);
        response.setIdentityDocument("987654321");

        StateResponse state = new StateResponse("Pending", "En revisión");
        LoanTypeResponse loanType = new LoanTypeResponse("Personal",
                new BigDecimal("50000"),
                new BigDecimal("1000000"),
                new BigDecimal("0.05"),
                false);

        response.setState(state);
        response.setLoanType(loanType);

        assertEquals("Pending", response.getState().getName());
        assertEquals("Personal", response.getLoanType().getName());
        assertFalse(response.getLoanType().getAutomaticValidation());
    }
}
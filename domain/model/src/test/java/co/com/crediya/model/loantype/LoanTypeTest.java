package co.com.crediya.model.loantype;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeTest {

    @Test
    void shouldBuildLoanTypeCorrectly() {
        LoanType loanType = LoanType.builder()
                .idLoanType(2)
                .name("Educativo")
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("0.03"))
                .automaticValidation(true)
                .build();

        assertEquals(2, loanType.getIdLoanType());
        assertEquals("Educativo", loanType.getName());
        assertEquals(new BigDecimal("100000"), loanType.getMinimumAmount());
        assertEquals(new BigDecimal("2000000"), loanType.getMaximumAmount());
        assertEquals(new BigDecimal("0.03"), loanType.getInterestRate());
        assertTrue(loanType.getAutomaticValidation());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        LoanType loanType = new LoanType();

        loanType.setIdLoanType(3);
        loanType.setName("Personal");
        loanType.setMinimumAmount(new BigDecimal("50000"));
        loanType.setMaximumAmount(new BigDecimal("1000000"));
        loanType.setInterestRate(new BigDecimal("0.05"));
        loanType.setAutomaticValidation(false);

        assertEquals(3, loanType.getIdLoanType());
        assertEquals("Personal", loanType.getName());
        assertEquals(new BigDecimal("50000"), loanType.getMinimumAmount());
        assertEquals(new BigDecimal("1000000"), loanType.getMaximumAmount());
        assertEquals(new BigDecimal("0.05"), loanType.getInterestRate());
        assertFalse(loanType.getAutomaticValidation());
    }
}
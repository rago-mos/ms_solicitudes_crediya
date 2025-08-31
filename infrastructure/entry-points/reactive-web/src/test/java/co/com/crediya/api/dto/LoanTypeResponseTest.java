package co.com.crediya.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeResponseTest {

    @Test
    void shouldBuildLoanTypeResponseCorrectly() {
        LoanTypeResponse response = LoanTypeResponse.builder()
                .name("Educativo")
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("0.03"))
                .automaticValidation(true)
                .build();

        assertEquals("Educativo", response.getName());
        assertEquals(new BigDecimal("100000"), response.getMinimumAmount());
        assertEquals(new BigDecimal("2000000"), response.getMaximumAmount());
        assertEquals(new BigDecimal("0.03"), response.getInterestRate());
        assertTrue(response.getAutomaticValidation());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        LoanTypeResponse response = new LoanTypeResponse();

        response.setName("Personal");
        response.setMinimumAmount(new BigDecimal("50000"));
        response.setMaximumAmount(new BigDecimal("1000000"));
        response.setInterestRate(new BigDecimal("0.05"));
        response.setAutomaticValidation(false);

        assertEquals("Personal", response.getName());
        assertEquals(new BigDecimal("50000"), response.getMinimumAmount());
        assertEquals(new BigDecimal("1000000"), response.getMaximumAmount());
        assertEquals(new BigDecimal("0.05"), response.getInterestRate());
        assertFalse(response.getAutomaticValidation());
    }
}
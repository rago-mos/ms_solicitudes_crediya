package co.com.crediya.r2dbc.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanTypeEntityTest {

    @Test
    void shouldBuildLoanTypeEntityCorrectly() {
        LoanTypeEntity entity = LoanTypeEntity.builder()
                .idLoanType(1)
                .name("Personal")
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("1000000"))
                .interestRate(new BigDecimal("0.05"))
                .automaticValidation(true)
                .build();

        assertEquals(1, entity.getIdLoanType());
        assertEquals("Personal", entity.getName());
        assertEquals(new BigDecimal("100000"), entity.getMinimumAmount());
        assertEquals(new BigDecimal("1000000"), entity.getMaximumAmount());
        assertEquals(new BigDecimal("0.05"), entity.getInterestRate());
        assertTrue(entity.getAutomaticValidation());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        LoanTypeEntity entity = new LoanTypeEntity();

        entity.setIdLoanType(2);
        entity.setName("Educativo");
        entity.setMinimumAmount(new BigDecimal("50000"));
        entity.setMaximumAmount(new BigDecimal("500000"));
        entity.setInterestRate(new BigDecimal("0.03"));
        entity.setAutomaticValidation(false);

        assertEquals(2, entity.getIdLoanType());
        assertEquals("Educativo", entity.getName());
        assertEquals(new BigDecimal("50000"), entity.getMinimumAmount());
        assertEquals(new BigDecimal("500000"), entity.getMaximumAmount());
        assertEquals(new BigDecimal("0.03"), entity.getInterestRate());
        assertFalse(entity.getAutomaticValidation());
    }
}
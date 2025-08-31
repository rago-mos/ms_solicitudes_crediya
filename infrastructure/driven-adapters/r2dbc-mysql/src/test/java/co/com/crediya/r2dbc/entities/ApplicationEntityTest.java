package co.com.crediya.r2dbc.entities;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationEntityTest {

    @Test
    void shouldBuildApplicationEntityCorrectly() {
        LocalDate date = LocalDate.of(2025, 8, 31);

        ApplicationEntity entity = ApplicationEntity.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("1000000"))
                .term(12)
                .identityDocument("123456789")
                .idState(1)
                .idLoanType(2)
                .date(date)
                .build();

        assertEquals("APP-001", entity.getIdApplication());
        assertEquals(new BigDecimal("1000000"), entity.getAmount());
        assertEquals(12, entity.getTerm());
        assertEquals("123456789", entity.getIdentityDocument());
        assertEquals(1, entity.getIdState());
        assertEquals(2, entity.getIdLoanType());
        assertEquals(date, entity.getDate());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        ApplicationEntity entity = new ApplicationEntity();
        LocalDate date = LocalDate.of(2025, 8, 31);

        entity.setIdApplication("APP-002");
        entity.setAmount(new BigDecimal("500000"));
        entity.setTerm(6);
        entity.setIdentityDocument("987654321");
        entity.setIdState(3);
        entity.setIdLoanType(4);
        entity.setDate(date);

        assertEquals("APP-002", entity.getIdApplication());
        assertEquals(new BigDecimal("500000"), entity.getAmount());
        assertEquals(6, entity.getTerm());
        assertEquals("987654321", entity.getIdentityDocument());
        assertEquals(3, entity.getIdState());
        assertEquals(4, entity.getIdLoanType());
        assertEquals(date, entity.getDate());
    }
}
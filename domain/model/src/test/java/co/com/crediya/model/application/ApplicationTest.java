package co.com.crediya.model.application;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {

    @Test
    void shouldBuildApplicationCorrectly() {
        State state = State.builder()
                .idState(1)
                .name("Approved")
                .description("Solicitud aprobada")
                .build();

        LoanType loanType = LoanType.builder()
                .idLoanType(2)
                .name("Educativo")
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("0.03"))
                .automaticValidation(true)
                .build();

        LocalDate date = LocalDate.of(2025, 8, 31);

        Application application = Application.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("1000000"))
                .term(12)
                .identityDocument("123456789")
                .state(state)
                .loanType(loanType)
                .date(date)
                .build();

        assertEquals("APP-001", application.getIdApplication());
        assertEquals(new BigDecimal("1000000"), application.getAmount());
        assertEquals(12, application.getTerm());
        assertEquals("123456789", application.getIdentityDocument());
        assertEquals(state, application.getState());
        assertEquals(loanType, application.getLoanType());
        assertEquals(date, application.getDate());

        assertEquals("Educativo", application.getLoanType().getName());
        assertEquals("Approved", application.getState().getName());
    }

    @Test
    void shouldSetAndGetFieldsCorrectly() {
        Application application = new Application();

        application.setIdApplication("APP-002");
        application.setAmount(new BigDecimal("500000"));
        application.setTerm(6);
        application.setIdentityDocument("987654321");

        State state = new State(2, "Pending", "En revisión");
        LoanType loanType = new LoanType(3, "Personal",
                new BigDecimal("50000"),
                new BigDecimal("1000000"),
                new BigDecimal("0.05"),
                false);

        application.setState(state);
        application.setLoanType(loanType);
        application.setDate(LocalDate.of(2025, 9, 1));

        assertEquals("APP-002", application.getIdApplication());
        assertEquals("Pending", application.getState().getName());
        assertEquals("Personal", application.getLoanType().getName());
        assertFalse(application.getLoanType().getAutomaticValidation());
    }
}
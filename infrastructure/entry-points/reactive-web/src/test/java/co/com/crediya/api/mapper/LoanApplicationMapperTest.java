package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.dto.LoanTypeResponse;
import co.com.crediya.api.dto.StateResponse;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanApplicationMapperTest {

    private LoanApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LoanApplicationMapper();
    }

    @Test
    void shouldMapRequestToModelCorrectly() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                new BigDecimal("1000000"),
                12,
                "123456789",
                2
        );

        Application model = mapper.toModel(request);

        assertEquals(request.amount(), model.getAmount());
        assertEquals(request.term(), model.getTerm());
        assertEquals(request.identityDocument(), model.getIdentityDocument());
        assertEquals(request.idLoanType(), model.getLoanType().getIdLoanType());
        assertEquals(1, model.getState().getIdState()); // buildState() siempre retorna idState = 1
    }

    @Test
    void shouldMapModelToResponseCorrectly() {
        Application model = Application.builder()
                .amount(new BigDecimal("500000"))
                .term(6)
                .identityDocument("987654321")
                .state(State.builder()
                        .idState(1)
                        .name("Approved")
                        .description("Solicitud aprobada")
                        .build())
                .loanType(LoanType.builder()
                        .idLoanType(2)
                        .name("Educativo")
                        .minimumAmount(new BigDecimal("100000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .interestRate(new BigDecimal("0.03"))
                        .automaticValidation(true)
                        .build())
                .build();

        LoanApplicationResponse response = mapper.toResponse(model);

        assertEquals(model.getAmount(), response.getAmount());
        assertEquals(model.getTerm(), response.getTerm());
        assertEquals(model.getIdentityDocument(), response.getIdentityDocument());

        StateResponse state = response.getState();
        assertEquals("Approved", state.getName());
        assertEquals("Solicitud aprobada", state.getDescription());

        LoanTypeResponse loanType = response.getLoanType();
        assertEquals("Educativo", loanType.getName());
        assertEquals(new BigDecimal("100000"), loanType.getMinimumAmount());
        assertEquals(new BigDecimal("2000000"), loanType.getMaximumAmount());
        assertEquals(new BigDecimal("0.03"), loanType.getInterestRate());
        assertTrue(loanType.getAutomaticValidation());
    }
}
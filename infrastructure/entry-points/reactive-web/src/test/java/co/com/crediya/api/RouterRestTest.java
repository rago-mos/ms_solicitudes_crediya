package co.com.crediya.api;


import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.dto.LoanTypeResponse;
import co.com.crediya.api.dto.StateResponse;
import co.com.crediya.api.exception.GlobalExceptionHandler;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private ILoanApplicationUseCase loanApplicationUseCase;

    @MockitoBean
    private LoanApplicationMapper loanApplicationMapper;

    @MockitoBean
    private Validator validator;

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {

        LoanApplicationRequest request = new LoanApplicationRequest(
                new BigDecimal("1000000"),
                12,
                "123456789",
                2
        );

        Application application = Application.builder()
                .amount(request.amount())
                .term(request.term())
                .identityDocument(request.identityDocument())
                .state(State.builder().idState(1).name("Approved").description("Approved application").build())
                .loanType(LoanType.builder()
                        .idLoanType(2)
                        .name("Personal")
                        .minimumAmount(new BigDecimal("50000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .interestRate(new BigDecimal("0.05"))
                        .automaticValidation(true)
                        .build())
                .date(LocalDate.now())
                .build();

        LoanApplicationResponse response = LoanApplicationResponse.builder()
                .amount(application.getAmount())
                .term(application.getTerm())
                .identityDocument(application.getIdentityDocument())
                .state(StateResponse.builder()
                        .name(application.getState().getName())
                        .description(application.getState().getDescription()).build())
                .loanType(LoanTypeResponse.builder()
                        .name(application.getLoanType().getName())
                        .interestRate(application.getLoanType().getInterestRate())
                        .minimumAmount(application.getLoanType().getMinimumAmount())
                        .maximumAmount(application.getLoanType().getMaximumAmount())
                        .automaticValidation(application.getLoanType().getAutomaticValidation())
                        .build())
                .build();

        when(validator.validate(any())).thenReturn(Set.of());
        when(loanApplicationMapper.toModel(request)).thenReturn(application);
        when(loanApplicationUseCase.registerLoanApplication(application)).thenReturn(Mono.just(application));
        when(loanApplicationMapper.toResponse(application)).thenReturn(response);

        // Act & Assert
        client.post()
                .uri("/api/v1/solicitud")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(LoanApplicationResponse.class)
                .value(r -> {
                    assert r.getAmount().equals(response.getAmount());
                    assert r.getTerm().equals(response.getTerm());
                    assert r.getIdentityDocument().equals(response.getIdentityDocument());
                    assert r.getState().getName().equals(response.getState().getName());
                    assert r.getLoanType().getName().equals(response.getLoanType().getName());
                });
    }
}

package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

class LoanApplicationUseCaseTest {

    private ApplicationRepository applicationRepository;
    private StateRepository stateRepository;
    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationValidator validator;

    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        stateRepository = mock(StateRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        validator = mock(LoanApplicationValidator.class);

        useCase = new LoanApplicationUseCase(applicationRepository, stateRepository, loanTypeRepository, validator);
    }

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {
        // Arrange
        Application application = Application.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("1000000"))
                .term(12)
                .identityDocument("123456789")
                .state(State.builder().idState(1).build())
                .loanType(LoanType.builder().idLoanType(2).build())
                .date(LocalDate.of(2025, 8, 31))
                .build();

        State enrichedState = State.builder()
                .idState(1)
                .name("Approved")
                .description("Solicitud aprobada")
                .build();

        LoanType enrichedLoanType = LoanType.builder()
                .idLoanType(2)
                .name("Educativo")
                .minimumAmount(new BigDecimal("50000"))
                .maximumAmount(new BigDecimal("2000000"))
                .interestRate(new BigDecimal("0.03"))
                .automaticValidation(true)
                .build();

        when(validator.validate(application)).thenReturn(Mono.empty());
        when(applicationRepository.registerApplication(application)).thenReturn(Mono.just(application));
        when(stateRepository.findState(1)).thenReturn(Mono.just(enrichedState));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(enrichedLoanType));

        // Act
        Mono<Application> result = useCase.registerLoanApplication(application);

        // Assert
        StepVerifier.create(result)
                .assertNext(app -> {
                    assert app.getState().getName().equals("Approved");
                    assert app.getLoanType().getName().equals("Educativo");
                    assert app.getLoanType().getInterestRate().compareTo(new BigDecimal("0.03")) == 0;
                })
                .verifyComplete();

        verify(validator).validate(application);
        verify(applicationRepository).registerApplication(application);
        verify(stateRepository).findState(1);
        verify(loanTypeRepository).findLoanType(2);
    }
}
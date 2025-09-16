package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.ApplicationAprovedView;
import co.com.crediya.model.application.dto.ApplicationValidationData;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsCapacityGateway;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.model.user.UserApplication;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;

class LoanApplicationUseCaseTest {

    private ApplicationRepository applicationRepository;
    private StateRepository stateRepository;
    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationValidator validator;
    private UserClientRepository userClientRepository;
    private SqsCapacityGateway sqsCapacityGateway;

    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        stateRepository = mock(StateRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        validator = mock(LoanApplicationValidator.class);
        userClientRepository = mock(UserClientRepository.class);
        sqsCapacityGateway = mock(SqsCapacityGateway.class);

        useCase = new LoanApplicationUseCase(applicationRepository, stateRepository, loanTypeRepository, validator,
                userClientRepository, sqsCapacityGateway);
    }

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {

        Application application = Application.builder()
                .idApplication(1L)
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

        UserApplication user = UserApplication.builder()
                .identityDocument("123456789")
                .firstName("Rubén Tester")
                .build();

        ApplicationAprovedView previousView = ApplicationAprovedView.builder()
                .amount(new BigDecimal("900000"))
                .term(12)
                .interest(new BigDecimal("0.03"))
                .loanTypeName("Educativo")
                .build();

        when(validator.validate(application, "shjdfhks")).thenReturn(Mono.empty());
        when(applicationRepository.registerApplication(application)).thenReturn(Mono.just(application));
        when(stateRepository.findState(1)).thenReturn(Mono.just(enrichedState));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(enrichedLoanType));
        when(userClientRepository.getUsersByDocuments(anyList(), eq("shjdfhks")))
                .thenReturn(Flux.just(user));
        when(applicationRepository.getApplicationsAproved("123456789"))
                .thenReturn(Flux.just(previousView));
        when(sqsCapacityGateway.send(any(ApplicationValidationData.class)))
                .thenReturn(Mono.just("msg-001"));

        Mono<Application> result = useCase.registerLoanApplication(application, "shjdfhks");

        StepVerifier.create(result)
                .assertNext(app -> {
                    assert app.getState().getName().equals("Approved");
                    assert app.getLoanType().getName().equals("Educativo");
                    assert app.getLoanType().getInterestRate().compareTo(new BigDecimal("0.03")) == 0;
                })
                .verifyComplete();

        verify(validator).validate(application, "shjdfhks");
        verify(applicationRepository).registerApplication(application);
        verify(stateRepository).findState(1);
        verify(loanTypeRepository).findLoanType(2);
    }

    @Test
    void shouldCalculateCapacitySuccessfully() {

        String token = "Bearer abc123";

        Application application = Application.builder()
                .idApplication(1L)
                .identityDocument("123456789")
                .amount(new BigDecimal("1000000"))
                .term(12)
                .state(State.builder().idState(1).build())
                .loanType(LoanType.builder().idLoanType(2).build())
                .build();

        Application enriched = application.toBuilder()
                .state(State.builder().idState(1).name("Pendiente").build())
                .loanType(LoanType.builder().idLoanType(2).name("Personal").interestRate(new BigDecimal("0.05")).build())
                .build();

        UserApplication user = UserApplication.builder()
                .identityDocument("123456789")
                .firstName("Rubén Tester")
                .build();

        ApplicationAprovedView previousView = ApplicationAprovedView.builder()
                .amount(new BigDecimal("900000"))
                .term(12)
                .interest(new BigDecimal("0.045"))
                .loanTypeName("Personal")
                .build();

        when(validator.validateCalculate(application)).thenReturn(Mono.empty());
        when(applicationRepository.getApplication(1L)).thenReturn(Mono.just(application));
        when(stateRepository.findState(1)).thenReturn(Mono.just(enriched.getState()));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(enriched.getLoanType()));
        when(userClientRepository.getUsersByDocuments(List.of("123456789"), token)).thenReturn(Flux.just(user));
        when(applicationRepository.getApplicationsAproved("123456789")).thenReturn(Flux.just(previousView));
        when(sqsCapacityGateway.send(any(ApplicationValidationData.class))).thenReturn(Mono.just("msg-001"));

        Mono<String> result = useCase.calculateCapacityApplication(application, token);

        StepVerifier.create(result)
                .expectNext("The capacity calculation for the request is in progress")
                .verifyComplete();

        verify(validator).validateCalculate(application);
        verify(applicationRepository).getApplication(1L);
        verify(stateRepository).findState(1);
        verify(loanTypeRepository).findLoanType(2);
        verify(userClientRepository).getUsersByDocuments(List.of("123456789"), token);
        verify(applicationRepository).getApplicationsAproved("123456789");
        verify(sqsCapacityGateway).send(any(ApplicationValidationData.class));
    }
}
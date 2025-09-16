package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsNotificationsGateway;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.model.user.UserApplication;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private UpdateApplicationValidator validator;
    @Mock
    private UserClientRepository userClientRepository;
    @Mock
    private SqsNotificationsGateway sqsRepository;

    private UpdateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateApplicationUseCase(applicationRepository, stateRepository, validator,
                userClientRepository, sqsRepository);
    }

    @Test
    void shouldUpdateApplicationDirectlyWhenStateIsThree() {

        StateApplication update = StateApplication.builder()
                .idApplication(1L)
                .idState(3)
                .build();

        Application updated = Application.builder()
                .idApplication(1L)
                .state(State.builder().idState(3).build())
                .build();

        when(validator.validate(update)).thenReturn(Mono.empty());
        when(applicationRepository.updateApplication(update)).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateApplication(update, "token123"))
                .expectNext("state updated successfully")
                .verifyComplete();

        verify(validator).validate(update);
        verify(applicationRepository).updateApplication(update);
        verifyNoInteractions(userClientRepository, sqsRepository);
    }

    @Test
    void shouldUpdateApplicationAndSendMessageWhenStateIsNotThree() {

        StateApplication update = StateApplication.builder()
                .idApplication(1L)
                .idState(2)
                .build();

        Application updated = Application.builder()
                .idApplication(1L)
                .amount(BigDecimal.valueOf(1000000))
                .identityDocument("123456789")
                .state(State.builder().idState(2).build())
                .loanType(LoanType.builder().idLoanType(1).build())
                .build();

        UserApplication user = UserApplication.builder()
                .firstName("Rubén")
                .lastName("Tester")
                .email("ruben@example.com")
                .identityDocument("123456789")
                .build();

        when(validator.validate(update)).thenReturn(Mono.empty());
        when(applicationRepository.updateApplication(update)).thenReturn(Mono.just(updated));
        when(userClientRepository.getUsersByDocuments(List.of("123456789"), "token123"))
                .thenReturn(Flux.just(user));
        when(sqsRepository.send(any(NotificationData.class))).thenReturn(Mono.just("msg-123"));

        StepVerifier.create(useCase.updateApplication(update, "token123"))
                .expectNext("state updated successfully")
                .verifyComplete();

        verify(validator).validate(update);
        verify(applicationRepository).updateApplication(update);
        verify(userClientRepository).getUsersByDocuments(List.of("123456789"), "token123");
        verify(sqsRepository).send(any(NotificationData.class));
    }

    @Test
    void shouldUpdateApplicationCalculateSuccessfully() {

        NotificationData data = NotificationData.builder()
                .idApplication(1L)
                .idStatus(3)
                .identityDocument("123456789")
                .fullName("Rubén")
                .email("ruben@example.com")
                .loanTypeName("Personal")
                .isValidatedAutomatic(true)
                .paymentPlans(List.of())
                .build();

        Application application = Application.builder()
                .idApplication(1L)
                .identityDocument("123456789")
                .build();

        State state = State.builder()
                .idState(3)
                .name("Aprobado")
                .build();

        Application updated = application.toBuilder().state(state).build();

        when(validator.validate(data)).thenReturn(Mono.empty());
        when(applicationRepository.getApplication(1L)).thenReturn(Mono.just(application));
        when(stateRepository.findState(3)).thenReturn(Mono.just(state));
        when(applicationRepository.registerApplication(any(Application.class)))
                .thenReturn(Mono.just(updated));
        when(sqsRepository.send(data)).thenReturn(Mono.just("msg-001"));

        Mono<Void> result = useCase.updateApplicationCalculate(data);

        StepVerifier.create(result)
                .verifyComplete();

        verify(validator).validate(data);
        verify(applicationRepository).getApplication(1L);
        verify(stateRepository).findState(3);
        verify(applicationRepository).registerApplication(argThat(app ->
                app.getState().getIdState().equals(3)
        ));
        verify(sqsRepository).send(data);
    }
}
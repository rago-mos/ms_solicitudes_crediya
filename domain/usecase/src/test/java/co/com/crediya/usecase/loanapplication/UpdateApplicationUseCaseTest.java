package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.UpdateStateApplication;
import co.com.crediya.model.application.dto.UpdateApplicationView;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsMessageRepository;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationUseCaseTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private UpdateApplicationValidator validator;
    @Mock
    private UserClientRepository userClientRepository;
    @Mock
    private SqsMessageRepository sqsRepository;

    private UpdateApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new UpdateApplicationUseCase(applicationRepository, validator, userClientRepository, sqsRepository);
    }

    @Test
    void shouldUpdateApplicationDirectlyWhenStateIsThree() {

        UpdateStateApplication update = UpdateStateApplication.builder()
                .idApplication("APP001")
                .idState(3)
                .build();

        Application updated = Application.builder()
                .idApplication("APP001")
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

        UpdateStateApplication update = UpdateStateApplication.builder()
                .idApplication("APP001")
                .idState(2)
                .build();

        Application updated = Application.builder()
                .idApplication("APP001")
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
        when(sqsRepository.send(any(UpdateApplicationView.class))).thenReturn(Mono.just("msg-123"));

        StepVerifier.create(useCase.updateApplication(update, "token123"))
                .expectNext("state updated successfully")
                .verifyComplete();

        verify(validator).validate(update);
        verify(applicationRepository).updateApplication(update);
        verify(userClientRepository).getUsersByDocuments(List.of("123456789"), "token123");
        verify(sqsRepository).send(any(UpdateApplicationView.class));
    }
}
package co.com.crediya.usecase.loanapplication.validator;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.UpdateStateApplication;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.NotFoundException;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateApplicationValidatorTest {

    @Mock
    private StateRepository stateRepository;
    @Mock
    private ApplicationRepository applicationRepository;

    private UpdateApplicationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UpdateApplicationValidator(stateRepository, applicationRepository);
    }

    private final UpdateStateApplication validRequest = UpdateStateApplication.builder()
            .idApplication("APP001")
            .idState(2)
            .build();

    @Test
    void shouldValidateSuccessfullyWhenAllConditionsAreMet() {
        Application application = Application.builder()
                .idApplication("APP001")
                .state(State.builder().idState(3).build())
                .build();

        when(stateRepository.existsState(2)).thenReturn(Mono.just(true));
        when(applicationRepository.existsApplication("APP001")).thenReturn(Mono.just(true));
        when(applicationRepository.getApplication("APP001")).thenReturn(Mono.just(application));

        StepVerifier.create(validator.validate(validRequest))
                .verifyComplete();

        verify(stateRepository).existsState(2);
        verify(applicationRepository).existsApplication("APP001");
        verify(applicationRepository).getApplication("APP001");
    }

    @Test
    void shouldFailWhenStateDoesNotExist() {
        when(stateRepository.existsState(2)).thenReturn(Mono.just(false));
        when(applicationRepository.existsApplication(anyString())).thenReturn(Mono.just(true));
        when(applicationRepository.getApplication(anyString())).thenReturn(Mono.just(Application.builder().build()));

        StepVerifier.create(validator.validate(validRequest))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals("State not found")
                )
                .verify();

        verify(stateRepository).existsState(2);
        verifyNoMoreInteractions(applicationRepository);
    }

    @Test
    void shouldFailWhenApplicationDoesNotExist() {
        when(stateRepository.existsState(2)).thenReturn(Mono.just(true));
        when(applicationRepository.existsApplication("APP001")).thenReturn(Mono.just(false));
        when(applicationRepository.getApplication(anyString())).thenReturn(Mono.just(Application.builder().build()));

        StepVerifier.create(validator.validate(validRequest))
                .expectErrorMatches(error ->
                        error instanceof NotFoundException &&
                                error.getMessage().equals("Application not found")
                )
                .verify();

        verify(applicationRepository).existsApplication("APP001");
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4}) // APROBADA o RECHAZADA
    void shouldFailWhenApplicationIsAlreadyApprovedOrRejected(int currentState) {
        Application application = Application.builder()
                .idApplication("APP001")
                .state(State.builder().idState(currentState).build())
                .build();

        when(stateRepository.existsState(2)).thenReturn(Mono.just(true));
        when(applicationRepository.existsApplication("APP001")).thenReturn(Mono.just(true));
        when(applicationRepository.getApplication("APP001")).thenReturn(Mono.just(application));

        StepVerifier.create(validator.validate(validRequest))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals("The application status is already approved or rejected")
                )
                .verify();
    }

    @Test
    void shouldFailWhenNewStateIsPendingReview() {
        UpdateStateApplication invalidRequest = validRequest.toBuilder().idState(1).build();

        Application application = Application.builder()
                .idApplication("APP001")
                .state(State.builder().idState(3).build())
                .build();

        when(stateRepository.existsState(1)).thenReturn(Mono.just(true));
        when(applicationRepository.existsApplication("APP001")).thenReturn(Mono.just(true));
        when(applicationRepository.getApplication("APP001")).thenReturn(Mono.just(application));

        StepVerifier.create(validator.validate(invalidRequest))
                .expectErrorMatches(error ->
                        error instanceof BusinessException &&
                                error.getMessage().equals("The state to update is not valid")
                )
                .verify();
    }
}

package co.com.crediya.consumer.exception;

import co.com.crediya.model.exception.AuthenticationServiceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AuthenticationFallbackHandlerTest {

    private AuthenticationFallbackHandler fallbackHandler;

    @BeforeEach
    void setUp() {
        fallbackHandler = new AuthenticationFallbackHandler();
    }

    @Test
    void shouldReturnControlledExceptionInFallbackFlux() {

        String operation = "getUsersByDocuments";
        Throwable originalException = new RuntimeException("Timeout");

        Flux<Object> result = fallbackHandler.fallbackFlux(operation, originalException);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof AuthenticationServiceUnavailableException &&
                                error.getMessage().contains("Authentication service is currently unavailable")
                )
                .verify();
    }
    @Test
    void shouldReturnControlledExceptionInFallbackMono() {
        String operation = "getUserById";
        Throwable originalException = new IllegalStateException("Connection refused");

        Mono<Object> result = fallbackHandler.fallbackMono(operation, originalException);

        StepVerifier.create(result)
                .expectErrorMatches(error ->
                        error instanceof AuthenticationServiceUnavailableException &&
                                error.getMessage().contains("Authentication service is currently unavailable")
                )
                .verify();
    }

}
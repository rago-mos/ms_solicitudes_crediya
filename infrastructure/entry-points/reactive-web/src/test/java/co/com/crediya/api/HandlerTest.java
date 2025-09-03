package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.model.application.Application;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private Validator validator;

    @Mock
    private ILoanApplicationUseCase loanApplicationUseCase;

    @Mock
    private LoanApplicationMapper loanApplicationMapper;

    @Mock
    private JwtProvider jwtProvider;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(validator, loanApplicationUseCase, loanApplicationMapper, jwtProvider);
    }

    @Test
    void shouldReturnCreatedWhenValidRequestAndMatchingDocument() {
        // Arrange
        String token = "valid-token";
        String subject = "123456789";
        LoanApplicationRequest requestDto = new LoanApplicationRequest(
                BigDecimal.valueOf(1000), 12, subject, 1);

        Application model = Application.builder().identityDocument(subject).build();
        Application created = model.toBuilder().idApplication("APP-001").build();
        LoanApplicationResponse response = LoanApplicationResponse.builder().identityDocument(subject).build();

        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Mono.just(requestDto));

        when(jwtProvider.getSubject(token)).thenReturn(subject);
        when(validator.validate(requestDto)).thenReturn(Collections.emptySet());
        when(loanApplicationMapper.toModel(requestDto)).thenReturn(model);
        when(loanApplicationUseCase.registerLoanApplication(model, token)).thenReturn(Mono.just(created));
        when(loanApplicationMapper.toResponse(created)).thenReturn(response);

        // Act
        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void shouldReturnForbiddenWhenDocumentMismatch() {
        // Arrange
        String token = "valid-token";
        String subject = "999999999";
        LoanApplicationRequest requestDto = new LoanApplicationRequest(
                BigDecimal.valueOf(1000), 12, "123456789", 1);

        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Mono.just(requestDto));

        when(jwtProvider.getSubject(token)).thenReturn(subject);

        // Act
        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.FORBIDDEN))
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {
        // Arrange
        String token = "valid-token";
        String subject = "123456789";
        LoanApplicationRequest requestDto = new LoanApplicationRequest(
                null, null, subject, null); // campos inválidos

        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Mono.just(requestDto));

        ConstraintViolation<LoanApplicationRequest> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path path = mock(jakarta.validation.Path.class);
        when(path.toString()).thenReturn("amount");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        when(jwtProvider.getSubject(token)).thenReturn(subject);
        when(validator.validate(requestDto)).thenReturn(Set.of(violation));

        // Act
        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().contains("amount: must not be null"))
                .verify();
    }
    }
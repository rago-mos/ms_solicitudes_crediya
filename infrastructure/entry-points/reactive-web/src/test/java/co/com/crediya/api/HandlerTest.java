package co.com.crediya.api;

import co.com.crediya.api.dto.LoanApplicationRequest;
import co.com.crediya.api.dto.LoanApplicationResponse;
import co.com.crediya.api.dto.UpdateApplicationRequest;
import co.com.crediya.api.mapper.LoanApplicationMapper;
import co.com.crediya.api.mapper.UpdateApplicationMapper;
import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.UpdateStateApplication;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.ILoanApplicationUseCase;
import co.com.crediya.usecase.loanapplication.IUpdateApplicationUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private Validator validator;

    @Mock
    private ILoanApplicationUseCase loanApplicationUseCase;

    @Mock
    private IUpdateApplicationUseCase updateApplicationUseCase;

    @Mock
    private LoanApplicationMapper loanApplicationMapper;

    @Mock
    private UpdateApplicationMapper updateApplicationMapper;

    @Mock
    private JwtProvider jwtProvider;

    private Handler handler;

    @BeforeEach
    void setUp() {
        handler = new Handler(validator, loanApplicationUseCase, updateApplicationUseCase,
                loanApplicationMapper, updateApplicationMapper, jwtProvider);
    }

    @Test
    void shouldReturnCreatedWhenValidRequestAndMatchingDocument() {

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

        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        StepVerifier.create(result)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void shouldReturnForbiddenWhenDocumentMismatch() {

        String token = "valid-token";
        String subject = "999999999";
        LoanApplicationRequest requestDto = new LoanApplicationRequest(
                BigDecimal.valueOf(1000), 12, "123456789", 1);

        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Mono.just(requestDto));

        when(jwtProvider.getSubject(token)).thenReturn(subject);

        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        StepVerifier.create(result)
                .expectNextMatches(res -> res.statusCode().equals(HttpStatus.FORBIDDEN))
                .verifyComplete();
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() {

        String token = "valid-token";
        String subject = "123456789";
        LoanApplicationRequest requestDto = new LoanApplicationRequest(
                null, null, subject, null);

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

        Mono<ServerResponse> result = handler.listenPOSTApplicationLoan(request);

        StepVerifier.create(result)
                .expectErrorMatches(error -> error instanceof IllegalArgumentException &&
                        error.getMessage().contains("amount: must not be null"))
                .verify();
    }

    @Test
    void shouldHandlePutApplicationLoanSuccessfully() {

        String token = "Bearer abc123";
        UpdateApplicationRequest request = new UpdateApplicationRequest("APP001", 3);
        UpdateStateApplication model = UpdateStateApplication.builder()
                .idApplication("APP001")
                .idState(3)
                .build();

        String expectedMessage = "state updated successfully";

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .uri(URI.create("/api/v1/solicitud"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(request));

        when(validator.validate(request)).thenReturn(Set.of()); // sin errores
        when(updateApplicationMapper.toModel(request)).thenReturn(model);
        when(updateApplicationUseCase.updateApplication(eq(model), eq("abc123")))
                .thenReturn(Mono.just(expectedMessage));

        Mono<ServerResponse> responseMono = handler.listenPutApplicationLoan(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenRequestIsInvalid() {

        String token = "Bearer abc123";
        UpdateApplicationRequest invalidRequest = new UpdateApplicationRequest(null, null);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .uri(URI.create("/api/v1/solicitud"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(invalidRequest));

        ConstraintViolation<UpdateApplicationRequest> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<UpdateApplicationRequest> violation2 = mock(ConstraintViolation.class);

        when(violation1.getPropertyPath()).thenReturn(PathImpl.createPathFromString("idApplication"));
        when(violation1.getMessage()).thenReturn("The field is mandatory");

        when(violation2.getPropertyPath()).thenReturn(PathImpl.createPathFromString("idState"));
        when(violation2.getMessage()).thenReturn("The field is mandatory");

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = Set.of(violation1, violation2);
        when(validator.validate(invalidRequest)).thenReturn(violations);

        Mono<ServerResponse> responseMono = handler.listenPutApplicationLoan(serverRequest);

        StepVerifier.create(responseMono)
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().contains("idApplication: The field is mandatory") &&
                                throwable.getMessage().contains("idState: The field is mandatory")
                )
                .verify();
    }

    @Test
    void shouldUpdateApplicationWithoutValidationWhenStateIsNotThree() {

        String token = "Bearer abc123";
        UpdateApplicationRequest request = new UpdateApplicationRequest("APP002", 2);
        UpdateStateApplication model = UpdateStateApplication.builder()
                .idApplication("APP002")
                .idState(2)
                .build();

        String expectedMessage = "state updated successfully";

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .uri(URI.create("/api/v1/solicitud"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(Mono.just(request));

        when(validator.validate(request)).thenReturn(Set.of());
        when(updateApplicationMapper.toModel(request)).thenReturn(model);
        when(updateApplicationUseCase.updateApplication(eq(model), eq("abc123")))
                .thenReturn(Mono.just(expectedMessage));

        Mono<ServerResponse> responseMono = handler.listenPutApplicationLoan(serverRequest);

        StepVerifier.create(responseMono)
                .expectNextMatches(response -> {
                    assertEquals(HttpStatus.OK, response.statusCode());
                    return true;
                })
                .verifyComplete();
    }
}
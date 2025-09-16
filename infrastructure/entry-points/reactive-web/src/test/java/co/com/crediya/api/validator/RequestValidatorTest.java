package co.com.crediya.api.validator;

import co.com.crediya.api.dto.request.LoanApplicationRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

class RequestValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldPassValidationSuccessfully() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                new BigDecimal("1000000"),
                12,
                "123456789",
                2
        );

        StepVerifier.create(RequestValidator.validate(request, validator))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationDueToMissingFields() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                null, // amount
                null, // term
                null, // identityDocument
                null  // idLoanType
        );

        StepVerifier.create(RequestValidator.validate(request, validator))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("amount: The field is mandatory") &&
                        e.getMessage().contains("term: The field is mandatory") &&
                        e.getMessage().contains("identityDocument: The field is mandatory") &&
                        e.getMessage().contains("idLoanType: The field is mandatory"))
                .verify();
    }

    @Test
    void shouldFailValidationDueToInvalidIdentityDocumentPattern() {
        LoanApplicationRequest request = new LoanApplicationRequest(
                new BigDecimal("500000"),
                6,
                "ABC123XYZ", // invalid pattern
                1
        );

        StepVerifier.create(RequestValidator.validate(request, validator))
                .expectErrorMatches(e -> e instanceof IllegalArgumentException &&
                        e.getMessage().contains("identityDocument: The field must contain only numeric digits and must not exceed 20 characters"))
                .verify();
    }

    @Test
    void shouldCoverPrivateConstructor() throws Exception {
        var constructor = RequestValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
package co.com.crediya.usecase.loanapplication.validator;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.exception.BusinessException;
import co.com.crediya.usecase.loanapplication.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

class LoanApplicationValidatorTest {

    private LoanTypeRepository loanTypeRepository;
    private StateRepository stateRepository;
    private UserClientRepository userClientRepository;

    private LoanApplicationValidator validator;

    private Application baseApplication;

    @BeforeEach
    void setUp() {
        loanTypeRepository = mock(LoanTypeRepository.class);
        stateRepository = mock(StateRepository.class);
        userClientRepository = mock(UserClientRepository.class);

        validator = new LoanApplicationValidator(loanTypeRepository, stateRepository, userClientRepository);

        baseApplication = Application.builder()
                .idApplication("APP-001")
                .amount(new BigDecimal("1000000"))
                .term(12)
                .identityDocument("123456789")
                .state(State.builder().idState(1).build())
                .loanType(LoanType.builder().idLoanType(2).build())
                .date(LocalDate.of(2025, 8, 31))
                .build();
    }

    @Test
    void shouldPassValidationSuccessfully() {
        LoanType loanType = LoanType.builder()
                .idLoanType(2)
                .minimumAmount(new BigDecimal("500000"))
                .maximumAmount(new BigDecimal("2000000"))
                .build();

        when(stateRepository.existsState(1)).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsLoanType(2)).thenReturn(Mono.just(true));
        when(userClientRepository.userExistsByDocument("123456789")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(loanType));

        StepVerifier.create(validator.validate(baseApplication))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenStateNotFound() {
        when(stateRepository.existsState(1)).thenReturn(Mono.just(false));
        when(loanTypeRepository.existsLoanType(anyInt())).thenReturn(Mono.just(true));
        when(userClientRepository.userExistsByDocument(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findLoanType(anyInt())).thenReturn(Mono.just(
                LoanType.builder()
                        .minimumAmount(new BigDecimal("100000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .build()
        ));

        StepVerifier.create(validator.validate(baseApplication))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        e.getMessage().equals("State not found"))
                .verify();
    }

    @Test
    void shouldFailWhenLoanTypeNotFound() {
        when(stateRepository.existsState(1)).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsLoanType(2)).thenReturn(Mono.just(false));
        when(userClientRepository.userExistsByDocument(anyString())).thenReturn(Mono.just(true));
        when(loanTypeRepository.findLoanType(anyInt())).thenReturn(Mono.just(
                LoanType.builder()
                        .minimumAmount(new BigDecimal("100000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .build()
        ));

        StepVerifier.create(validator.validate(baseApplication))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        e.getMessage().equals("The loan type does not exist"))
                .verify();
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(stateRepository.existsState(1)).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsLoanType(2)).thenReturn(Mono.just(true));
        when(userClientRepository.userExistsByDocument("123456789")).thenReturn(Mono.just(false));
        when(loanTypeRepository.findLoanType(anyInt())).thenReturn(Mono.just(
                LoanType.builder()
                        .minimumAmount(new BigDecimal("100000"))
                        .maximumAmount(new BigDecimal("2000000"))
                        .build()
        ));

        StepVerifier.create(validator.validate(baseApplication))
                .expectErrorMatches(e -> e instanceof NotFoundException &&
                        e.getMessage().equals("User does not exist"))
                .verify();
    }

    @Test
    void shouldFailWhenAmountIsOutOfRange() {
        LoanType loanType = LoanType.builder()
                .idLoanType(2)
                .minimumAmount(new BigDecimal("100000"))
                .maximumAmount(new BigDecimal("500000"))
                .build();

        when(stateRepository.existsState(1)).thenReturn(Mono.just(true));
        when(loanTypeRepository.existsLoanType(2)).thenReturn(Mono.just(true));
        when(userClientRepository.userExistsByDocument("123456789")).thenReturn(Mono.just(true));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(loanType));

        StepVerifier.create(validator.validate(baseApplication))
                .expectErrorMatches(e -> e instanceof BusinessException &&
                        e.getMessage().contains("The amount is not valid"))
                .verify();
    }
}
package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import co.com.crediya.model.application.gateways.ApplicationRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LoanApplicationUseCaseTest {

    private ApplicationRepository applicationRepository;
    private StateRepository stateRepository;
    private LoanTypeRepository loanTypeRepository;
    private LoanApplicationValidator validator;
    private UserClientRepository userClientRepository;

    private LoanApplicationUseCase useCase;

    @BeforeEach
    void setUp() {
        applicationRepository = mock(ApplicationRepository.class);
        stateRepository = mock(StateRepository.class);
        loanTypeRepository = mock(LoanTypeRepository.class);
        validator = mock(LoanApplicationValidator.class);
        userClientRepository = mock(UserClientRepository.class);

        useCase = new LoanApplicationUseCase(applicationRepository, stateRepository, loanTypeRepository, validator,
                userClientRepository);
    }

    @Test
    void shouldRegisterLoanApplicationSuccessfully() {

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

        when(validator.validate(application, "shjdfhks")).thenReturn(Mono.empty());
        when(applicationRepository.registerApplication(application)).thenReturn(Mono.just(application));
        when(stateRepository.findState(1)).thenReturn(Mono.just(enrichedState));
        when(loanTypeRepository.findLoanType(2)).thenReturn(Mono.just(enrichedLoanType));

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
    void shouldReturnEnrichedPageApplicationResponse() {

        List<Integer> status = List.of(1, 2);
        int page = 1;
        int size = 10;
        int offset = 0;
        String token = "abc123";

        LoanApplicationView view1 = LoanApplicationView.builder()
                .identityDocument("123456789")
                .amount(new BigDecimal("1000000"))
                .monthTerm(12)
                .monthAmountApprovedApplication(new BigDecimal("85000"))
                .statusName("Approved")
                .interestRate(new BigDecimal("0.05"))
                .loanTypeName("Personal")
                .build();

        LoanApplicationView view2 = view1.toBuilder().identityDocument("987654321").build();

        UserApplication user1 = UserApplication.builder()
                .identityDocument("123456789")
                .firstName("Rubén")
                .lastName("Tester")
                .email("ruben@example.com")
                .baseSalary(new BigDecimal("3000000"))
                .build();

        UserApplication user2 = UserApplication.builder()
                .identityDocument("987654321")
                .firstName("Ana")
                .lastName("Dev")
                .email("ana@example.com")
                .baseSalary(new BigDecimal("2500000"))
                .build();

        when(applicationRepository.countByStatus(status)).thenReturn(Mono.just(2L));
        when(applicationRepository.findLoanApplicationDetails(status, size, offset)).thenReturn(Flux.just(view1, view2));
        when(userClientRepository.getUsersByDocuments(List.of("123456789", "987654321"), token))
                .thenReturn(Flux.just(user1, user2));

        Mono<PageApplicationResponse<LoanApplicationView>> result = useCase.getLoanApplication(status, page, size, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getPage()).isEqualTo(page);
                    assertThat(response.getSize()).isEqualTo(size);
                    assertThat(response.getTotalElements()).isEqualTo(2L);
                    assertThat(response.getTotalPages()).isEqualTo(1);
                    assertThat(response.getContent()).hasSize(2);

                    LoanApplicationView enriched1 = response.getContent().get(0);
                    assertThat(enriched1.getIdentityDocument()).isEqualTo("123456789");
                    assertThat(enriched1.getEmail()).isEqualTo("ruben@example.com");
                    assertThat(enriched1.getBaseSalary()).isEqualByComparingTo("3000000");
                    assertThat(enriched1.getFullName()).isEqualTo("Rubén Tester");

                    LoanApplicationView enriched2 = response.getContent().get(1);
                    assertThat(enriched2.getIdentityDocument()).isEqualTo("987654321");
                    assertThat(enriched2.getEmail()).isEqualTo("ana@example.com");
                    assertThat(enriched2.getBaseSalary()).isEqualByComparingTo("2500000");
                    assertThat(enriched2.getFullName()).isEqualTo("Ana Dev");
                })
                .verifyComplete();

        verify(applicationRepository).countByStatus(status);
        verify(applicationRepository).findLoanApplicationDetails(status, size, offset);
        verify(userClientRepository).getUsersByDocuments(List.of("123456789", "987654321"), token);
    }

    @Test
    void shouldIgnoreDuplicateDocumentsAndEnrichOnce() {
        List<Integer> status = List.of(1);
        int page = 1;
        int size = 10;
        String token = "abc123";

        LoanApplicationView view1 = LoanApplicationView.builder()
                .identityDocument("123456789")
                .amount(new BigDecimal("1000000"))
                .monthTerm(12)
                .build();

        LoanApplicationView view2 = view1.toBuilder().identityDocument("123456789").build(); // mismo documento

        UserApplication user = UserApplication.builder()
                .identityDocument("123456789")
                .firstName("Rubén")
                .lastName("Tester")
                .email("ruben@example.com")
                .baseSalary(new BigDecimal("3000000"))
                .build();

        when(applicationRepository.countByStatus(status)).thenReturn(Mono.just(2L));
        when(applicationRepository.findLoanApplicationDetails(status, size, 0)).thenReturn(Flux.just(view1, view2));
        when(userClientRepository.getUsersByDocuments(List.of("123456789"), token)).thenReturn(Flux.just(user));

        Mono<PageApplicationResponse<LoanApplicationView>> result = useCase.getLoanApplication(status, page, size, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getContent()).hasSize(2);
                    response.getContent().forEach(app -> {
                        assertThat(app.getEmail()).isEqualTo("ruben@example.com");
                        assertThat(app.getFullName()).isEqualTo("Rubén Tester");
                    });
                })
                .verifyComplete();

        verify(userClientRepository).getUsersByDocuments(List.of("123456789"), token);
    }

    @Test
    void shouldReturnUnenrichedApplicationsWhenUsersNotFound() {
        List<Integer> status = List.of(1);
        int page = 1;
        int size = 10;
        String token = "abc123";

        LoanApplicationView view = LoanApplicationView.builder()
                .identityDocument("000000000")
                .amount(new BigDecimal("500000"))
                .monthTerm(6)
                .build();

        when(applicationRepository.countByStatus(status)).thenReturn(Mono.just(1L));
        when(applicationRepository.findLoanApplicationDetails(status, size, 0)).thenReturn(Flux.just(view));
        when(userClientRepository.getUsersByDocuments(List.of("000000000"), token)).thenReturn(Flux.empty());

        Mono<PageApplicationResponse<LoanApplicationView>> result = useCase.getLoanApplication(status, page, size, token);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getContent()).hasSize(1);
                    assertThat(response.getContent().get(0).getEmail()).isNull();
                    assertThat(response.getContent().get(0).getFullName()).isNull();
                })
                .verifyComplete();
    }


}
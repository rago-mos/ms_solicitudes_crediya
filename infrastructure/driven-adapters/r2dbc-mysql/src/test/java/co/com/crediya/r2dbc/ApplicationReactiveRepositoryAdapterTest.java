package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.entities.LoanApplicationViewEntity;
import co.com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationReactiveRepositoryAdapterTest {

    @Mock
    private ApplicationReactiveRepository repository;

    @Mock
    private LoanApplicationEntityMapper mapper;

    @Mock
    private ObjectMapper objectMapper;

    private ApplicationReactiveRepositoryAdapter adapter;

    private final Application application = Application.builder()
            .idApplication("APP-001")
            .amount(new BigDecimal("1000000"))
            .term(12)
            .identityDocument("123456789")
            .state(State.builder().idState(1).build())
            .loanType(LoanType.builder().idLoanType(2).build())
            .date(LocalDate.of(2025, 8, 31))
            .build();

    private final ApplicationEntity entity = ApplicationEntity.builder()
            .idApplication("APP-001")
            .amount(new BigDecimal("1000000"))
            .term(12)
            .identityDocument("123456789")
            .idState(1)
            .idLoanType(2)
            .date(LocalDate.of(2025, 8, 31))
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new ApplicationReactiveRepositoryAdapter(repository, objectMapper, mapper);
    }

    @Test
    void shouldRegisterApplicationSuccessfully() {

        when(mapper.toEntity(application)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(application);

        Mono<Application> result = adapter.registerApplication(application);

        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(mapper).toEntity(application);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldReturnLoanApplicationViewsFromRepository() {

        List<Integer> status = List.of(1, 2);
        int limit = 10;
        int offset = 0;

        LoanApplicationViewEntity entity1 = LoanApplicationViewEntity.builder()
                .identityDocument("123456789")
                .amount(new BigDecimal("1000000"))
                .monthTerm(12)
                .monthAmountApprovedApplication(new BigDecimal("85000"))
                .statusName("Approved")
                .interestRate(new BigDecimal("0.05"))
                .loanTypeName("Personal")
                .baseSalary(new BigDecimal("3000000"))
                .build();

        LoanApplicationViewEntity entity2 = entity1.toBuilder().identityDocument("987654321").build();

        LoanApplicationView view1 = LoanApplicationView.builder()
                .identityDocument("123456789")
                .amount(entity1.getAmount())
                .monthTerm(entity1.getMonthTerm())
                .monthAmountApprovedApplication(entity1.getMonthAmountApprovedApplication())
                .statusName(entity1.getStatusName())
                .interestRate(entity1.getInterestRate())
                .loanTypeName(entity1.getLoanTypeName())
                .baseSalary(entity1.getBaseSalary())
                .build();

        LoanApplicationView view2 = view1.toBuilder().identityDocument("987654321").build();

        when(repository.findLoanApplicationDetails(status, limit, offset))
                .thenReturn(Flux.just(entity1, entity2));

        when(mapper.toView(entity1)).thenReturn(view1);
        when(mapper.toView(entity2)).thenReturn(view2);

        Flux<LoanApplicationView> result = adapter.findLoanApplicationDetails(status, limit, offset);

        StepVerifier.create(result)
                .expectNext(view1)
                .expectNext(view2)
                .verifyComplete();

        verify(repository).findLoanApplicationDetails(status, limit, offset);
        verify(mapper).toView(entity1);
        verify(mapper).toView(entity2);
    }

    @Test
    void shouldReturnCountByStatusFromRepository() {

        List<Integer> status = List.of(1, 2);
        when(repository.countByStatusIn(status)).thenReturn(Mono.just(5L));

        Mono<Long> result = adapter.countByStatus(status);

        StepVerifier.create(result)
                .expectNext(5L)
                .verifyComplete();

        verify(repository).countByStatusIn(status);
    }

    @Test
    void shouldReturnTrueWhenApplicationExists() {
        // Arrange
        String id = "APP-001";
        when(repository.existsApplicationByIdApplication(id)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = adapter.existsApplication(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsApplicationByIdApplication(id);
    }

    @Test
    void shouldReturnApplicationByIdSuccessfully() {
        // Arrange
        String id = "APP-001";
        when(repository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(application);

        // Act
        Mono<Application> result = adapter.getApplication(id);

        // Assert
        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(repository).findById(id);
        verify(mapper).toDomain(entity);
    }

    @Test
    void shouldUpdateApplicationStateSuccessfully() {
        // Arrange
        StateApplication update = StateApplication.builder()
                .idApplication("APP-001")
                .idState(3)
                .build();

        ApplicationEntity updatedEntity = entity;
            updatedEntity.builder()
                .idState(3)
                .build();

        Application updatedDomain = application.toBuilder()
                .state(State.builder().idState(3).build())
                .build();

        when(repository.findById("APP-001")).thenReturn(Mono.just(entity));
        when(repository.save(updatedEntity)).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDomain(updatedEntity)).thenReturn(updatedDomain);

        // Act
        Mono<Application> result = adapter.updateApplication(update);

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedDomain)
                .verifyComplete();

        verify(repository).findById("APP-001");
        verify(repository).save(updatedEntity);
        verify(mapper).toDomain(updatedEntity);
    }


}

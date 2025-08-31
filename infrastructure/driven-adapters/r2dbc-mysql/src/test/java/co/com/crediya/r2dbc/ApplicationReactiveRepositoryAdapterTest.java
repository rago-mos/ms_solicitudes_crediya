package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        // Arrange
        when(mapper.toEntity(application)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(application);

        // Act
        Mono<Application> result = adapter.registerApplication(application);

        // Assert
        StepVerifier.create(result)
                .expectNext(application)
                .verifyComplete();

        verify(mapper).toEntity(application);
        verify(repository).save(entity);
        verify(mapper).toDomain(entity);
    }

}

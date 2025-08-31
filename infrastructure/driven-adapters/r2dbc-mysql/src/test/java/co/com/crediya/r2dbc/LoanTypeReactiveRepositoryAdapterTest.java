package co.com.crediya.r2dbc;

import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.r2dbc.entities.LoanTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class LoanTypeReactiveRepositoryAdapterTest {

    private LoanTypeReactiveRepository repository;
    private ObjectMapper mapper;
    private LoanTypeReactiveRepositoryAdapter adapter;

    private final LoanTypeEntity entity = LoanTypeEntity.builder()
            .idLoanType(1)
            .name("Personal")
            .minimumAmount(new BigDecimal("100000"))
            .maximumAmount(new BigDecimal("1000000"))
            .interestRate(new BigDecimal("0.05"))
            .automaticValidation(true)
            .build();

    private final LoanType domain = LoanType.builder()
            .idLoanType(1)
            .name("Personal")
            .minimumAmount(new BigDecimal("100000"))
            .maximumAmount(new BigDecimal("1000000"))
            .interestRate(new BigDecimal("0.05"))
            .automaticValidation(true)
            .build();

    @BeforeEach
    void setUp() {
        repository = mock(LoanTypeReactiveRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new LoanTypeReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldFindLoanTypeSuccessfully() {
        when(repository.findById(1)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, LoanType.class)).thenReturn(domain);

        Mono<LoanType> result = adapter.findLoanType(1);

        StepVerifier.create(result)
                .expectNext(domain)
                .verifyComplete();

        verify(repository).findById(1);
        verify(mapper).map(entity, LoanType.class);
    }

    @Test
    void shouldReturnErrorWhenLoanTypeNotFound() {
        when(repository.findById(99)).thenReturn(Mono.empty());

        Mono<LoanType> result = adapter.findLoanType(99);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof Exception &&
                        e.getMessage().equals("loan type not found"))
                .verify();

        verify(repository).findById(99);
        verify(mapper, never()).map(any(), eq(LoanType.class));
    }

    @Test
    void shouldReturnTrueWhenLoanTypeExists() {
        when(repository.existsById(1)).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existsLoanType(1);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(1);
    }

    @Test
    void shouldReturnFalseWhenLoanTypeDoesNotExist() {
        when(repository.existsById(2)).thenReturn(Mono.just(false));

        Mono<Boolean> result = adapter.existsLoanType(2);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsById(2);
    }
}
package co.com.crediya.r2dbc;

import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.StateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class StateReactiveRepositoryAdapterTest {

    private StateReactiveRepository repository;
    private ObjectMapper mapper;
    private StateReactiveRepositoryAdapter adapter;

    private final StateEntity entity = StateEntity.builder()
            .idState(1)
            .name("Approved")
            .description("Application approved")
            .build();

    private final State domain = State.builder()
            .idState(1)
            .name("Approved")
            .description("Application approved")
            .build();

    @BeforeEach
    void setUp() {
        repository = mock(StateReactiveRepository.class);
        mapper = mock(ObjectMapper.class);
        adapter = new StateReactiveRepositoryAdapter(repository, mapper);
    }

    @Test
    void shouldFindStateSuccessfully() {
        when(repository.findById(1)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, State.class)).thenReturn(domain);

        Mono<State> result = adapter.findState(1);

        StepVerifier.create(result)
                .expectNext(domain)
                .verifyComplete();

        verify(repository).findById(1);
        verify(mapper).map(entity, State.class);
    }

    @Test
    void shouldReturnErrorWhenStateNotFound() {
        when(repository.findById(99)).thenReturn(Mono.empty());

        Mono<State> result = adapter.findState(99);

        StepVerifier.create(result)
                .expectErrorMatches(e -> e instanceof Exception &&
                        e.getMessage().equals("state not found"))
                .verify();

        verify(repository).findById(99);
        verify(mapper, never()).map(any(), eq(State.class));
    }

    @Test
    void shouldReturnTrueWhenStateExists() {
        when(repository.existsById(1)).thenReturn(Mono.just(true));

        Mono<Boolean> result = adapter.existsState(1);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(1);
    }

    @Test
    void shouldReturnFalseWhenStateDoesNotExist() {
        when(repository.existsById(2)).thenReturn(Mono.just(false));

        Mono<Boolean> result = adapter.existsState(2);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(repository).existsById(2);
    }
}
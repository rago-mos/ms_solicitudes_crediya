package co.com.crediya.r2dbc;


import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.r2dbc.entities.StateEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class StateReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        State,
        StateEntity,
        Integer,
        StateReactiveRepository
> implements StateRepository {

    public StateReactiveRepositoryAdapter(StateReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, State.class));
    }


    @Override
    public Mono<State> findState(Integer id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new Exception("state not found")))
                .map(entity -> mapper.map(entity, State.class));
    }

    @Override
    public Mono<Boolean> existsState(Integer id) {
        return repository.existsById(id);
    }
}

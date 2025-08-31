package co.com.crediya.r2dbc;


import co.com.crediya.model.state.State;
import co.com.crediya.r2dbc.entities.StateEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface StateReactiveRepository extends ReactiveCrudRepository<StateEntity, Integer>, ReactiveQueryByExampleExecutor<StateEntity> {

    Mono<State> findByName(String name);
}

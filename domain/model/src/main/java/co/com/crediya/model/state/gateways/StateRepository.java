package co.com.crediya.model.state.gateways;


import co.com.crediya.model.state.State;
import reactor.core.publisher.Mono;

public interface StateRepository {

    Mono<State> findState(Integer id);
    Mono<Boolean> existsState(Integer id);

}

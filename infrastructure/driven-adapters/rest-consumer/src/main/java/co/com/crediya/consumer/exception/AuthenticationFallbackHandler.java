package co.com.crediya.consumer.exception;

import co.com.crediya.model.exception.AuthenticationServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static co.com.crediya.model.utils.Constant.*;

@Slf4j
@Component
public class AuthenticationFallbackHandler {

    public <T> Flux<T> fallbackFlux(String operation, Throwable throwable) {
        log.error(LOG_ERROR_SERVICE_UNAVAILABLE, operation, throwable.toString());
        return Flux.error(new AuthenticationServiceUnavailableException(
                ERROR_SERVICE_UNAVAILABLE));
    }

    public <T> Mono<T> fallbackMono(String operation, Throwable throwable) {
        log.error(LOG_ERROR_SERVICE_UNAVAILABLE, operation, throwable.toString());
        return Mono.error(new AuthenticationServiceUnavailableException(
                ERROR_SERVICE_UNAVAILABLE));
    }

}

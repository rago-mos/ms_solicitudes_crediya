package co.com.crediya.consumer;

import co.com.crediya.model.application.gateways.UserClientRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer implements UserClientRepository {

    private final WebClient client;

    @CircuitBreaker(name = "listenGetUserByDocument")
    @Override
    public Mono<Boolean> userExistsByDocument(String documentIdentity, String token) {
        log.info("Validating user by document number: {}", documentIdentity);
        return client
                .get()
                .uri("/api/v1/usuarios/{documentIdentity}", documentIdentity)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(ObjectResponse.class)
                .map(ObjectResponse::getExists);
    }
}

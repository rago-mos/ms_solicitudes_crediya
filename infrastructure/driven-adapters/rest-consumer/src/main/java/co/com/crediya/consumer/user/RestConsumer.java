package co.com.crediya.consumer.user;

import co.com.crediya.consumer.exception.AuthenticationFallbackHandler;
import co.com.crediya.consumer.user.mapper.UserRestMapper;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.user.UserApplication;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.crediya.model.utils.Constant.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer implements UserClientRepository {

    private final WebClient client;
    private final UserRestMapper mapper;
    private final AuthenticationFallbackHandler fallbackHandler;

    @CircuitBreaker(name = "listenGetUserByDocument", fallbackMethod = "fallbackUserExistsByDocument")
    @Override
    public Mono<Boolean> userExistsByDocument(String documentIdentity, String token) {
        log.info(LOG_VALIDATING_USER, documentIdentity);
        return client
                .get()
                .uri(URL_CONSUMER_USER_DOCUMENT, documentIdentity)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .retrieve()
                .bodyToMono(UserObjectResponse.class)
                .map(UserObjectResponse::getExists);
    }

    @CircuitBreaker(name = "listenPostUserApplications", fallbackMethod = "fallbackGetUsersByDocuments")
    @Override
    public Flux<UserApplication> getUsersByDocuments(List<String> document, String token) {
        log.info(LOG_QUERY_USER);
        Mono<UserDocumentsRequest> request = Mono.just(new UserDocumentsRequest(document));

        return client.post()
                .uri(URL_CONSUMER_USER_APPLICATION)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(token))
                .body(request, UserDocumentsRequest.class)
                .retrieve()
                .bodyToFlux(UserDocumentsResponse.class)
                .map(mapper::toUserApplication);
    }

    public Flux<UserApplication> fallbackGetUsersByDocuments(List<String> document, String token, Throwable throwable) {
        return fallbackHandler.fallbackFlux("getUsersByDocuments", throwable);
    }
    public Mono<Boolean> fallbackUserExistsByDocument(String documentIdentity, String token, Throwable throwable) {
        return fallbackHandler.fallbackMono("userExistsByDocument", throwable);
    }

}

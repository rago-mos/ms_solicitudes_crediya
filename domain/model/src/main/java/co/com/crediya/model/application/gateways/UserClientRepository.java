package co.com.crediya.model.application.gateways;

import reactor.core.publisher.Mono;

public interface UserClientRepository {

    Mono<Boolean> userExistsByDocument(String document);
}

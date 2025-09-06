package co.com.crediya.model.application.gateways;

import co.com.crediya.model.user.UserApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserClientRepository {

    Mono<Boolean> userExistsByDocument(String document, String token);
    Flux<UserApplication> getUsersByDocuments(List<String> document, String token);
}

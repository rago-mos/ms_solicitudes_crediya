package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.Application;
import reactor.core.publisher.Mono;

public interface ApplicationRepository {

    Mono<Application> registerApplication(Application application);
}

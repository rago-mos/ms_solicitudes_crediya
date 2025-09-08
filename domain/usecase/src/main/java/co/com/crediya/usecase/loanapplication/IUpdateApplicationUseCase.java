package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.UpdateStateApplication;
import reactor.core.publisher.Mono;

public interface IUpdateApplicationUseCase {

    Mono<String> updateApplication(UpdateStateApplication updateApplication, String token);
}

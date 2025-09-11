package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.StateApplication;
import reactor.core.publisher.Mono;

public interface IUpdateApplicationUseCase {

    Mono<String> updateApplication(StateApplication updateApplication, String token);
}

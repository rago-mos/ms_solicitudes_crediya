package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.NotificationData;
import reactor.core.publisher.Mono;

public interface IUpdateApplicationUseCase {

    Mono<String> updateApplication(StateApplication updateApplication, String token);
    Mono<Void> updateApplicationCalculate(NotificationData data);
}

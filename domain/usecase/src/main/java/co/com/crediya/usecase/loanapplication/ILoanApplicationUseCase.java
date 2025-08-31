package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import reactor.core.publisher.Mono;

public interface ILoanApplicationUseCase {

    Mono<Application> registerLoanApplication(Application application);
}

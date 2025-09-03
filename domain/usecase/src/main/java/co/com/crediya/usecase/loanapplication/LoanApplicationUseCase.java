package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements ILoanApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final StateRepository stateRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanApplicationValidator validator;


    @Override
    public Mono<Application> registerLoanApplication(Application application, String token) {
        return validator.validate(application, token)
                .then(applicationRepository.registerApplication(application))
                .flatMap(this::setApplication);
    }

    private Mono<Application> setApplication(Application application) {
        Mono<State> stateMono = stateRepository.findState(application.getState().getIdState());
        Mono<LoanType> loanTypeMono = loanTypeRepository.findLoanType(application.getLoanType().getIdLoanType());

        return Mono.zip(stateMono, loanTypeMono)
                .map(tuple -> {
                    application.setState(tuple.getT1());
                    application.setLoanType(tuple.getT2());
                    return application;
                });
    }

}

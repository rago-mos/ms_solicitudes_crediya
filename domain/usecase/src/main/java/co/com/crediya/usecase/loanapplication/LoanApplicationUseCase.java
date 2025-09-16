package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.*;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsCapacityGateway;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.model.user.UserApplication;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static co.com.crediya.model.utils.Constant.MESSAGE_OK_CALCULATE_CAPACITY;

@RequiredArgsConstructor
public class LoanApplicationUseCase implements ILoanApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final StateRepository stateRepository;
    private final LoanTypeRepository loanTypeRepository;
    private final LoanApplicationValidator validator;
    private final UserClientRepository userClientRepository;
    private final SqsCapacityGateway sqsCapacityGateway;


    @Override
    public Mono<Application> registerLoanApplication(Application application, String token) {
        return validator.validate(application, token)
                .then(applicationRepository.registerApplication(application))
                .flatMap(this::setApplication)
                .flatMap(applicationFinal ->
                        Boolean.TRUE.equals(applicationFinal.getLoanType().getAutomaticValidation())
                        ? automaticValidation(applicationFinal, token)
                        : Mono.just(applicationFinal)
                );
    }

    @Override
    public Mono<String> calculateCapacityApplication(Application application, String token) {
        return validator.validateCalculate(application)
                .then(applicationRepository.getApplication(application.getIdApplication()))
                .flatMap(this::setApplication)
                .flatMap(applicationFinal -> automaticValidation(applicationFinal, token))
                .thenReturn(MESSAGE_OK_CALCULATE_CAPACITY);
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

    private Mono<Application> automaticValidation(Application application, String token) {

        List<String> documents = Collections.singletonList(application.getIdentityDocument());

        Mono<UserApplication> user = userClientRepository.getUsersByDocuments(documents, token).next();
        Mono<List<ApplicationAprovedView>> applicationsAproved = applicationRepository
                .getApplicationsAproved(application.getIdentityDocument()).collectList();

        return Mono.zip(user, applicationsAproved)
                .flatMap(tuple -> Mono.just(ApplicationValidationData.builder()
                        .idApplication(application.getIdApplication())
                        .user(tuple.getT1())
                        .applicationsAproved(tuple.getT2())
                        .ApplicationNew(ApplicationAprovedView.builder()
                                .amount(application.getAmount())
                                .term(application.getTerm())
                                .interest(application.getLoanType().getInterestRate())
                                .loanTypeName(application.getLoanType().getName())
                                .build())
                        .build()))
                .flatMap(sqsCapacityGateway::send)
                .thenReturn(application);
    }
}

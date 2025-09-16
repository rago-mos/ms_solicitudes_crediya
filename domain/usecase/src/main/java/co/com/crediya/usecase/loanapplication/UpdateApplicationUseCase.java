package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.NotificationData;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsNotificationsGateway;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.LoanType;
import co.com.crediya.model.loantype.enums.LoanTypeEnum;
import co.com.crediya.model.state.State;
import co.com.crediya.model.state.enums.StateEnum;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static co.com.crediya.model.utils.Constant.MESSAGE_UPDATED_APPLICATION;

@RequiredArgsConstructor
public class UpdateApplicationUseCase implements IUpdateApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final StateRepository stateRepository;
    private final UpdateApplicationValidator validator;
    private final UserClientRepository userClientRepository;
    private final SqsNotificationsGateway sqsNotificationsGateway;

    @Override
    public Mono<String> updateApplication(StateApplication application, String token) {

        if (application.getIdState() == 3) {
            return validator.validate(application)
                    .then(Mono.defer(() -> applicationRepository.updateApplication(application)))
                    .flatMap(success -> Mono.just(MESSAGE_UPDATED_APPLICATION));
        }

        return validator.validate(application)
                .then(Mono.defer(() -> applicationRepository.updateApplication(application)))
                .flatMap(updatedApplication -> getInformationUser(updatedApplication, token))
                .flatMap(sqsNotificationsGateway::send)
                .thenReturn(MESSAGE_UPDATED_APPLICATION);
    }

    private Mono<NotificationData> getInformationUser(Application application, String token) {

        List<String> documents = Collections.singletonList(application.getIdentityDocument());

        return userClientRepository.getUsersByDocuments(documents, token)
                .next()
                .map(user -> NotificationData.builder()
                        .idApplication(application.getIdApplication())
                        .identityDocument(application.getIdentityDocument())
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .email(user.getEmail())
                        .loanTypeName(LoanTypeEnum.getNameById(application.getLoanType().getIdLoanType()))
                        .statusName(StateEnum.getNameById(application.getState().getIdState()))
                        .idStatus(application.getState().getIdState())
                        .isValidatedAutomatic(false)
                        .build()
                );
    }


    @Override
    public Mono<Void> updateApplicationCalculate(NotificationData data) {
        return validator.validate(data)
                .then(applicationRepository.getApplication(data.getIdApplication()))
                .flatMap(application -> this.setApplication(application, data.getIdStatus()))
                .flatMap(applicationRepository::registerApplication)
                .then(sqsNotificationsGateway.send(data))
                .then(Mono.empty());
    }

    private Mono<Application> setApplication(Application application, Integer idState) {
        return stateRepository.findState(idState)
                .map(state -> {
                    application.setState(state);
                    return application;
                });
    }

}

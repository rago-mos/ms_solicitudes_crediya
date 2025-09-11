package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.UpdateApplicationView;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.SqsMessageRepository;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.enums.LoanTypeEnum;
import co.com.crediya.model.state.enums.StateEnum;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static co.com.crediya.model.utils.Constant.MESSAGE_UPDATED_APPLICATION;

@RequiredArgsConstructor
public class UpdateApplicationUseCase implements IUpdateApplicationUseCase {

    private final ApplicationRepository applicationRepository;
    private final UpdateApplicationValidator validator;
    private final UserClientRepository userClientRepository;
    private final SqsMessageRepository sqsRepository;

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
                .flatMap(sqsRepository::send)
                .thenReturn(MESSAGE_UPDATED_APPLICATION);
    }


    private Mono<UpdateApplicationView> getInformationUser(Application application, String token) {

        List<String> documents = Collections.singletonList(application.getIdentityDocument());

        return userClientRepository.getUsersByDocuments(documents, token)
                .next()
                .map(user -> UpdateApplicationView.builder()
                        .idApplication(application.getIdApplication())
                        .identityDocument(application.getIdentityDocument())
                        .amount(application.getAmount())
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .email(user.getEmail())
                        .loanTypeName(LoanTypeEnum.getNameById(application.getLoanType().getIdLoanType()))
                        .statusName(StateEnum.getNameById(application.getState().getIdState()))
                        .build()
                );
    }

}

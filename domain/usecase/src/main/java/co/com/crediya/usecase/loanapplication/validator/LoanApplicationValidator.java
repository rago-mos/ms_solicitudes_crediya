package co.com.crediya.usecase.loanapplication.validator;


import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.exception.BusinessException;
import co.com.crediya.usecase.loanapplication.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class LoanApplicationValidator {

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final UserClientRepository userClientRepository;

    public Mono<Void> validate(Application application) {
        return validateExistsState(application)
                .then(validateExistsLoanType(application))
                .then(validateUserExists(application))
                .then(validateAmount(application));
    }

    private Mono<Void> validateExistsState(Application application) {
        return stateRepository.existsState(application.getState().getIdState())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException("State not found")));
    }

    private Mono<Void> validateExistsLoanType(Application application) {
        return loanTypeRepository.existsLoanType(application.getLoanType().getIdLoanType())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException("The loan type does not exist")));
    }

    private Mono<Void> validateAmount(Application application) {
        return loanTypeRepository.findLoanType(application.getLoanType().getIdLoanType())
                .flatMap(loanType -> {
                    BigDecimal amount = application.getAmount();
                    if (amount.compareTo(loanType.getMinimumAmount()) >= 0 &&
                            amount.compareTo(loanType.getMaximumAmount()) <= 0) {
                        return Mono.empty();
                    }
                    return Mono.error(new BusinessException(
                            String.format("The amount is not valid; it must be between %s and %s",
                                    loanType.getMinimumAmount(), loanType.getMaximumAmount())));
                });
    }

    private Mono<Void> validateUserExists(Application application) {
        return userClientRepository.userExistsByDocument(application.getIdentityDocument())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException("User does not exist")));
    }


}

package co.com.crediya.usecase.loanapplication.validator;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.model.exception.BusinessException;
import co.com.crediya.model.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static co.com.crediya.model.utils.Constant.*;

@RequiredArgsConstructor
public class LoanApplicationValidator {

    private final LoanTypeRepository loanTypeRepository;
    private final StateRepository stateRepository;
    private final UserClientRepository userClientRepository;

    public Mono<Void> validate(Application application, String token) {
        return validateExistsState(application)
                .then(validateExistsLoanType(application))
                .then(validateUserExists(application, token))
                .then(validateAmount(application));
    }

    private Mono<Void> validateExistsState(Application application) {
        return stateRepository.existsState(application.getState().getIdState())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException(STATE_ERROR)));
    }

    private Mono<Void> validateExistsLoanType(Application application) {
        return loanTypeRepository.existsLoanType(application.getLoanType().getIdLoanType())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException(LOAN_TYPE_ERROR)));
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
                            String.format(AMOUNT_ERROR, loanType.getMinimumAmount(), loanType.getMaximumAmount())));
                });
    }

    private Mono<Void> validateUserExists(Application application, String token) {
        return userClientRepository.userExistsByDocument(application.getIdentityDocument(), token)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.empty()
                        : Mono.error(new NotFoundException(USER_ERROR)));
    }


}

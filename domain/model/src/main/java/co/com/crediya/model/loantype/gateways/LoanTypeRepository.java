package co.com.crediya.model.loantype.gateways;

import co.com.crediya.model.loantype.LoanType;
import reactor.core.publisher.Mono;

public interface LoanTypeRepository {

    Mono<LoanType> findLoanType(Integer id);
    Mono<Boolean> existsLoanType(Integer id);

}

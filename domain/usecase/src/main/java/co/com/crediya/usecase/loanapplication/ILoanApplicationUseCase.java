package co.com.crediya.usecase.loanapplication;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.dto.PageApplicationResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ILoanApplicationUseCase {

    Mono<Application> registerLoanApplication(Application application, String token);
    Mono<PageApplicationResponse<LoanApplicationView>> getLoanApplication(List<Integer> status, int page, int size, String token);
}

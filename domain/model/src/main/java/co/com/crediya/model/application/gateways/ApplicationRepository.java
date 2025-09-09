package co.com.crediya.model.application.gateways;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.UpdateStateApplication;
import co.com.crediya.model.application.dto.LoanApplicationView;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApplicationRepository {

    Mono<Application> registerApplication(Application application);
    Flux<LoanApplicationView> findLoanApplicationDetails(List<Integer> status, int limit, int offset);
    Mono<Long> countByStatus(List<Integer> status);
    Mono<Boolean> existsApplication(String id);
    Mono<Application> updateApplication(UpdateStateApplication application);
    Mono<Application> getApplication(String id);
}

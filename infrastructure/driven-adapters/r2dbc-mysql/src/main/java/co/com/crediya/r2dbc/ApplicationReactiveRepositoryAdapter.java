package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.StateApplication;
import co.com.crediya.model.application.dto.LoanApplicationView;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class ApplicationReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Application,
        ApplicationEntity,
        String,
        ApplicationReactiveRepository
> implements ApplicationRepository {

    private final LoanApplicationEntityMapper loanApplicationEntityMapper;

    public ApplicationReactiveRepositoryAdapter(ApplicationReactiveRepository repository, ObjectMapper mapper,
                                                LoanApplicationEntityMapper loanApplicationEntityMapper) {

        super(repository, mapper, d -> mapper.map(d, Application.class));
        this.loanApplicationEntityMapper = loanApplicationEntityMapper;
    }

    @Override
    public Mono<Application> registerApplication(Application application) {
        return repository.save(loanApplicationEntityMapper.toEntity(application))
                .map(loanApplicationEntityMapper::toDomain);
    }

    @Override
    public Flux<LoanApplicationView> findLoanApplicationDetails(List<Integer> status, int limit, int offset) {
        return repository.findLoanApplicationDetails(status, limit, offset)
                .map(loanApplicationEntityMapper::toView);
    }

    @Override
    public Mono<Long> countByStatus(List<Integer> status) {
        return repository.countByStatusIn(status);
    }

    @Override
    public Mono<Boolean> existsApplication(String id) {
        return repository.existsApplicationByIdApplication(id);
    }

    @Override
    public Mono<Application> updateApplication(StateApplication application) {
        return repository.findById(application.getIdApplication())
                .flatMap(applicationEntity -> {
                    applicationEntity.setIdState(application.getIdState());
                    return repository.save(applicationEntity);
                })
                .map(loanApplicationEntityMapper::toDomain);
    }

    @Override
    public Mono<Application> getApplication(String id) {
        return repository.findById(id)
                .map(loanApplicationEntityMapper::toDomain);
    }


}

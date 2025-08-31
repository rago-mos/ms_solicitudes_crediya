package co.com.crediya.r2dbc;

import co.com.crediya.model.application.Application;
import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.r2dbc.mapper.LoanApplicationEntityMapper;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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
}

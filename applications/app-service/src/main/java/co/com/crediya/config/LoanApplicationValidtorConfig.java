package co.com.crediya.config;

import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoanApplicationValidtorConfig {

    @Bean
    public LoanApplicationValidator loanApplicationValidator(LoanTypeRepository loanTypeRepository,
                                                             StateRepository stateRepository,
                                                             UserClientRepository userClientRepository) {
        return new LoanApplicationValidator(loanTypeRepository, stateRepository, userClientRepository);
    }
}

package co.com.crediya.config;

import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateApplicationValidatorConfig {

    @Bean
    public UpdateApplicationValidator updateApplicationValidator(StateRepository stateRepository,
                                                                 ApplicationRepository applicationRepository) {
        return new UpdateApplicationValidator(stateRepository, applicationRepository);
    }
}

package co.com.crediya.config;

import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UpdateApplicationValidatorConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(UpdateApplicationValidatorConfig.class)
            .withBean(StateRepository.class, () -> mock(StateRepository.class))
            .withBean(ApplicationRepository.class, () -> mock(ApplicationRepository.class));

    @Test
    void shouldRegisterUpdateApplicationValidatorBean() {
        contextRunner.run(context -> {
            assertTrue(context.containsBean("updateApplicationValidator"));

            UpdateApplicationValidator validator = context.getBean(UpdateApplicationValidator.class);
            assertNotNull(validator);
        });
    }
}
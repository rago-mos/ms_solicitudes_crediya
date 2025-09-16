package co.com.crediya.config;

import co.com.crediya.model.application.gateways.ApplicationRepository;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanApplicationValidatorConfigTest {

    @Test
    void shouldCreateLoanApplicationValidatorBeanSuccessfully() {

        LoanTypeRepository loanTypeRepository = mock(LoanTypeRepository.class);
        StateRepository stateRepository = mock(StateRepository.class);
        UserClientRepository userClientRepository = mock(UserClientRepository.class);
        ApplicationRepository applicationRepository = mock(ApplicationRepository.class);

        LoanApplicationValidtorConfig config = new LoanApplicationValidtorConfig();

        LoanApplicationValidator validator = config.loanApplicationValidator(
                loanTypeRepository, stateRepository, userClientRepository,
                applicationRepository);

        assertNotNull(validator);
    }
}
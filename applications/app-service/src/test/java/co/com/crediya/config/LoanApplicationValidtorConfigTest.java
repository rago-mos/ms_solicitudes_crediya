package co.com.crediya.config;

import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.model.loantype.gateways.LoanTypeRepository;
import co.com.crediya.model.state.gateways.StateRepository;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanApplicationValidtorConfigTest {

    @Test
    void shouldCreateLoanApplicationValidatorBeanSuccessfully() {
        // Arrange: mock dependencies
        LoanTypeRepository loanTypeRepository = mock(LoanTypeRepository.class);
        StateRepository stateRepository = mock(StateRepository.class);
        UserClientRepository userClientRepository = mock(UserClientRepository.class);

        LoanApplicationValidtorConfig config = new LoanApplicationValidtorConfig();

        // Act
        LoanApplicationValidator validator = config.loanApplicationValidator(
                loanTypeRepository, stateRepository, userClientRepository);

        // Assert
        assertNotNull(validator);
    }
}
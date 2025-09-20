package co.com.crediya.config;

import co.com.crediya.model.application.gateways.SqsMessageGateway;
import co.com.crediya.model.application.gateways.UserClientRepository;
import co.com.crediya.r2dbc.ApplicationReactiveRepository;
import co.com.crediya.r2dbc.LoanTypeReactiveRepository;
import co.com.crediya.r2dbc.StateReactiveRepository;
import co.com.crediya.r2dbc.config.MysqlConnectionProperties;
import co.com.crediya.security.provider.JwtProvider;
import co.com.crediya.usecase.loanapplication.validator.LoanApplicationValidator;
import co.com.crediya.usecase.loanapplication.validator.UpdateApplicationValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    @ComponentScan(basePackages = {
            "co.com.crediya.usecase",
            "co.com.crediya.r2dbc",
            "co.com.crediya.model"
    })
    static class TestConfig {

        @Bean
        public UserClientRepository userClientRepository() {
            return Mockito.mock(UserClientRepository.class);
        }

        @Bean
        public SqsMessageGateway sqsMessageGateway() {
            return Mockito.mock(SqsMessageGateway.class);
        }


        @Bean
        public JwtProvider  jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }

        @Bean
        public LoanApplicationValidator loanApplicationValidator() {
            return Mockito.mock(LoanApplicationValidator.class);
        }

        @Bean
        public UpdateApplicationValidator updateApplicationValidator() {
            return Mockito.mock(UpdateApplicationValidator.class);
        }

        @Bean
        public ApplicationReactiveRepository applicationReactiveRepository() {
            return Mockito.mock(ApplicationReactiveRepository.class);
        }

        @Bean
        public LoanTypeReactiveRepository loanTypeReactiveRepository() {
            return Mockito.mock(LoanTypeReactiveRepository.class);
        }

        @Bean
        public StateReactiveRepository stateReactiveRepository() {
            return Mockito.mock(StateReactiveRepository.class);
        }

        @Bean
        public MysqlConnectionProperties mysqlConnectionProperties() {
            return new MysqlConnectionProperties(
                    "localhost",
                    3306,
                    "testdb",
                    "test",
                    "test",
                    "test"
            );
        }

        @Bean
        public org.reactivecommons.utils.ObjectMapper reactiveCommonsObjectMapper() {
            return new org.reactivecommons.utils.ObjectMapper() {
                @Override
                public <T> T map(Object src, Class<T> target) {
                    return null;
                }

                @Override
                public <T> T mapBuilder(Object src, Class<T> target) {
                    return null;
                }
            };
        }

        @Bean
        public MyUseCase myUseCase() {
            return new MyUseCase();
        }
    }

    static class MyUseCase {
        public String execute() {
            return "MyUseCase Test";
        }
    }
}
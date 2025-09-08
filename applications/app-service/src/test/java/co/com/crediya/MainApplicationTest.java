package co.com.crediya;

import co.com.crediya.r2dbc.ApplicationReactiveRepository;
import co.com.crediya.r2dbc.LoanTypeReactiveRepository;
import co.com.crediya.r2dbc.StateReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@SpringBootTest(
        classes = MainApplication.class,
        properties = {
                "jwt.secret=my-super-secret-key-12345678901234567890123456789012",
                "adapter.sqs.region=us-east-1",
                "adapter.sqs.queueUrl=https://sqs.us-east-1.amazonaws.com/123/my-queue",
                "adapter.sqs.endpoint=https://sqs.us-east-1.amazonaws.com",
                "server.port=8002",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration",
                "spring.flyway.enabled=false",
        }
)
class MainApplicationTest {

    @MockitoBean
    private SqsAsyncClient sqsAsyncClient;

    @MockitoBean
    private ApplicationReactiveRepository applicationReactiveRepository;

    @MockitoBean
    private StateReactiveRepository stateReactiveRepository;

    @MockitoBean
    private LoanTypeReactiveRepository  loanTypeReactiveRepository;

    @Test
    void contextLoads() {
    }

}
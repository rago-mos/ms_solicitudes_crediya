package co.com.crediya.sqs.sender.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SQSSenderConfigTest {

    private SQSSenderConfig config;

    @BeforeEach
    void setUp() {
        config = new SQSSenderConfig();
    }

    @Test
    void shouldCreateSqsAsyncClientWithProperties() {

        SQSSenderProperties props = new SQSSenderProperties("us-east-1", "https://sqs.mock.aws");
        MetricPublisher publisher = mock(MetricPublisher.class);

        SqsAsyncClient client = config.configSqs(props, publisher);

        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().region()).isEqualTo(Region.of("us-east-1"));
        assertThat(client.serviceClientConfiguration().overrideConfiguration().metricPublishers())
                .contains(publisher);
    }

    @Test
    void shouldResolveEndpointWhenPresent() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = SQSSenderConfig.class.getDeclaredMethod("resolveEndpoint", SQSSenderProperties.class);
        method.setAccessible(true);
        SQSSenderProperties props = new SQSSenderProperties("us-east-1", "https://sqs.mock.aws");

        URI endpoint = (URI) method.invoke(new SQSSenderConfig(), props);
        assertThat(endpoint).isEqualTo(URI.create("https://sqs.mock.aws"));
    }

    @Test
    void shouldReturnNullEndpointWhenMissing() throws Exception {

        Method method = SQSSenderConfig.class.getDeclaredMethod("resolveEndpoint", SQSSenderProperties.class);
        method.setAccessible(true);
        SQSSenderProperties props = new SQSSenderProperties("us-east-1", null);

        URI endpoint = (URI) method.invoke(new SQSSenderConfig(), props);
        assertThat(endpoint).isNull();
    }

    @Test
    void shouldBuildAwsCredentialsProviderChain() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = SQSSenderConfig.class.getDeclaredMethod("getProviderChain");
        method.setAccessible(true);

        AwsCredentialsProviderChain chain = (AwsCredentialsProviderChain) method.invoke(new SQSSenderConfig());

        assertThat(chain).isNotNull();
    }
}

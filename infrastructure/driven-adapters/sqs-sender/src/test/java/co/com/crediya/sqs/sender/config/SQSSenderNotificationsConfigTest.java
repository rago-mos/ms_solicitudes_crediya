package co.com.crediya.sqs.sender.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SQSSenderNotificationsConfigTest {

    @Test
    void shouldCreateSqsAsyncClientBeanSuccessfully() {

        SQSSenderNotificationsProperties properties = new SQSSenderNotificationsProperties(
                "us-east-1",
                "https://sqs.us-east-1.amazonaws.com/123456789012/my-queue",
                "https://sqs.us-east-1.amazonaws.com"
        );

        MetricPublisher publisher = mock(MetricPublisher.class);
        SQSSenderConfig config = new SQSSenderConfig();

        SqsAsyncClient client = config.configSqs(properties, publisher);

        assertNotNull(client);
        assertEquals(Region.US_EAST_1, client.serviceClientConfiguration().region());
        assertEquals(
                URI.create("https://sqs.us-east-1.amazonaws.com"),
                client.serviceClientConfiguration().endpointOverride().orElseThrow()
        );

    }

    @Test
    void shouldCreateSqsAsyncClientWithoutEndpointWhenNotProvided() {

        SQSSenderNotificationsProperties properties = new SQSSenderNotificationsProperties(
                "us-east-1",
                "https://sqs.us-east-1.amazonaws.com/123456789012/my-queue",
                null
        );

        MetricPublisher publisher = mock(MetricPublisher.class);
        SQSSenderConfig config = new SQSSenderConfig();

        SqsAsyncClient client = config.configSqs(properties, publisher);

        assertNotNull(client);
        assertEquals(Region.US_EAST_1, client.serviceClientConfiguration().region());

        Optional<URI> endpoint = client.serviceClientConfiguration().endpointOverride();
        assertTrue(endpoint.isEmpty(), "Expected endpointOverride to be empty when endpoint is null");
    }

}
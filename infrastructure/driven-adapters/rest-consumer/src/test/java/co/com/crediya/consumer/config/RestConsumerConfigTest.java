package co.com.crediya.consumer.config;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class RestConsumerConfigTest {

    @Test
    void shouldCreateWebClientSuccessfully() {
        // Arrange
        String expectedUrl = "https://api.crediya.com";
        int expectedTimeout = 5000;
        RestConsumerConfig config = new RestConsumerConfig(expectedUrl, expectedTimeout);

        WebClient.Builder builder = WebClient.builder();

        // Act
        WebClient client = config.getWebClient(builder);

        // Assert
        assertNotNull(client);

        // Verifica que el WebClient puede construir una request con el baseUrl
        var request = client.get().uri("/test");
        assertNotNull(request);

    }

    @Test
    void shouldSendRequestWithExpectedHeaders() throws IOException, InterruptedException {
        MockWebServer server = new MockWebServer();
        server.start();

        String baseUrl = server.url("/").toString();
        RestConsumerConfig config = new RestConsumerConfig(baseUrl, 5000);
        WebClient client = config.getWebClient(WebClient.builder());

        server.enqueue(new MockResponse().setBody("OK"));

        client.get().uri("/test").retrieve().bodyToMono(String.class).block();

        RecordedRequest request = server.takeRequest(1, TimeUnit.SECONDS);
        assertEquals("application/json", request.getHeader(HttpHeaders.CONTENT_TYPE));

        server.shutdown();
    }
}
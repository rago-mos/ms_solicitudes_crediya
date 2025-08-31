package co.com.crediya.consumer;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


class RestConsumerTest {

    private static RestConsumer restConsumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("userExistsByDocument: devuelve true cuando el backend responde exists=true")
    void shouldReturnTrueWhenUserExists() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"exists\": true}"));

        var mono = restConsumer.userExistsByDocument("123456");

        StepVerifier.create(mono)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockBackEnd.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getMethod()).isEqualTo("GET");
        Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/123456");
    }

    @Test
    @DisplayName("userExistsByDocument: devuelve false cuando el backend responde exists=false")
    void shouldReturnFalseWhenUserDoesNotExist() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"exists\": false}"));

        var mono = restConsumer.userExistsByDocument("999");

        StepVerifier.create(mono)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockBackEnd.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getMethod()).isEqualTo("GET");
        Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/999");
    }

    @Test
    @DisplayName("userExistsByDocument: propaga error cuando el backend responde 404")
    void shouldPropagateErrorWhenUserNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("{\"title\":\"Not Found\",\"detail\":\"User not found\"}"));

        var mono = restConsumer.userExistsByDocument("999");

        StepVerifier.create(mono)
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(WebClientResponseException.class);
                    WebClientResponseException ex = (WebClientResponseException) throwable;
                    Assertions.assertThat(ex.getStatusCode().value()).isEqualTo(404);
                })
                .verify();
    }
}
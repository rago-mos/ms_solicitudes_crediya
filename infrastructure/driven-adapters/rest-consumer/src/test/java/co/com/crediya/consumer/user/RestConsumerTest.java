package co.com.crediya.consumer.user;


import co.com.crediya.consumer.user.mapper.UserRestMapper;
import co.com.crediya.model.user.UserApplication;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.test.StepVerifier;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestConsumerTest {

    private RestConsumer restConsumer;
    private MockWebServer mockBackEnd;
    @Mock
    private UserRestMapper userRestMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockBackEnd.url("/").toString())
                .build();
        restConsumer = new RestConsumer(webClient, userRestMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    void shouldReturnTrueWhenUserExists() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"exists\": true}"));

        var mono = restConsumer.userExistsByDocument("123456", "iwuefiuwehfw");

        StepVerifier.create(mono)
                .expectNext(true)
                .verifyComplete();

        RecordedRequest request = mockBackEnd.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getMethod()).isEqualTo("GET");
        Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/123456");
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\"exists\": false}"));

        var mono = restConsumer.userExistsByDocument("999", "iwuefiuwehfw");

        StepVerifier.create(mono)
                .expectNext(false)
                .verifyComplete();

        RecordedRequest request = mockBackEnd.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getMethod()).isEqualTo("GET");
        Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/usuarios/999");
    }

    @Test
    void shouldPropagateErrorWhenUserNotFound() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("{\"title\":\"Not Found\",\"detail\":\"User not found\"}"));

        var mono = restConsumer.userExistsByDocument("999", "iwuefiuwehfw");

        StepVerifier.create(mono)
                .expectErrorSatisfies(throwable -> {
                    Assertions.assertThat(throwable)
                            .isInstanceOf(WebClientResponseException.class);
                    WebClientResponseException ex = (WebClientResponseException) throwable;
                    Assertions.assertThat(ex.getStatusCode().value()).isEqualTo(404);
                })
                .verify();
    }

    @Test
    void shouldReturnUserApplicationsSuccessfully() throws InterruptedException {

        List<String> documents = List.of("123456789", "987654321");
        String token = "abc123";

        String responseBody = """
        [
            {
                "firstName": "Rubén",
                "lastName": "Tester",
                "email": "ruben@example.com",
                "identityDocument": "123456789",
                "baseSalary": 3000000
            },
            {
                "firstName": "Ana",
                "lastName": "Dev",
                "email": "ana@example.com",
                "identityDocument": "987654321",
                "baseSalary": 2500000
            }
        ]
        """;

        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody(responseBody));

        when(userRestMapper.toUserApplication(any())).thenAnswer(invocation -> {
            UserDocumentsResponse res = invocation.getArgument(0);
            return UserApplication.builder()
                    .firstName(res.getFirstName())
                    .lastName(res.getLastName())
                    .email(res.getEmail())
                    .identityDocument(res.getIdentityDocument())
                    .baseSalary(res.getBaseSalary())
                    .build();
        });

        var flux = restConsumer.getUsersByDocuments(documents, token);

        StepVerifier.create(flux)
                .expectNextMatches(user -> user.getIdentityDocument().equals("123456789"))
                .expectNextMatches(user -> user.getIdentityDocument().equals("987654321"))
                .verifyComplete();

        RecordedRequest request = mockBackEnd.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertThat(request).isNotNull();
        Assertions.assertThat(request.getMethod()).isEqualTo("POST");
        Assertions.assertThat(request.getPath()).isEqualTo("/api/v1/usuarioSolicitudes");
        Assertions.assertThat(request.getHeader(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + token);
        Assertions.assertThat(request.getBody().readUtf8()).contains("123456789", "987654321");
    }
}
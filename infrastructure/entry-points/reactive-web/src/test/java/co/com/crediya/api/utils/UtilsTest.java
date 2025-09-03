package co.com.crediya.api.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

    @Test
    void shouldExtractTokenWhenHeaderIsValid() {
        String token = "abc123";
        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();

        String extracted = Utils.extractToken(request);

        assertThat(extracted).isEqualTo(token);
    }

    @Test
    void shouldReturnNullWhenHeaderIsMissing() {
        ServerRequest request = MockServerRequest.builder().build();

        String extracted = Utils.extractToken(request);

        assertThat(extracted).isNull();
    }

    @Test
    void shouldReturnNullWhenHeaderDoesNotStartWithBearer() {
        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Basic abc123")
                .build();

        String extracted = Utils.extractToken(request);

        assertThat(extracted).isNull();
    }

    @Test
    void shouldReturnNullWhenHeaderIsEmpty() {
        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "")
                .build();

        String extracted = Utils.extractToken(request);

        assertThat(extracted).isNull();
    }

    @Test
    void shouldReturnNullWhenBearerHasNoToken() {
        ServerRequest request = MockServerRequest.builder()
                .header(HttpHeaders.AUTHORIZATION, "Bearer ")
                .build();

        String extracted = Utils.extractToken(request);

        assertThat(extracted).isEmpty();
    }
}
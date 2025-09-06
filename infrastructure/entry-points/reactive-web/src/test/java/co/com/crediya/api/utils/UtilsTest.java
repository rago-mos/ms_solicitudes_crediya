package co.com.crediya.api.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    @Test
    void shouldExtractValidStatusList() {
        ServerRequest request = MockServerRequest.builder()
                .queryParam("status", "1,2,3")
                .build();

        List<Integer> result = Utils.extractStatus(request);

        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    void shouldTrimAndParseStatusValues() {
        ServerRequest request = MockServerRequest.builder()
                .queryParam("status", " 4 , 5 ")
                .build();

        List<Integer> result = Utils.extractStatus(request);

        assertThat(result).containsExactly(4, 5);
    }

    @Test
    void shouldReturnSingleValue() {
        ServerRequest request = MockServerRequest.builder()
                .queryParam("status", "7")
                .build();

        List<Integer> result = Utils.extractStatus(request);

        assertThat(result).containsExactly(7);
    }

    @Test
    void shouldReturnDefaultWhenStatusMissing() {
        ServerRequest request = MockServerRequest.builder()
                .uri(URI.create("/api/v1/solicitud"))
                .build();

        List<Integer> result = Utils.extractStatus(request);

        assertThat(result).containsExactly(1);
    }

    @Test
    void shouldThrowExceptionForInvalidStatusValue() {
        ServerRequest request = MockServerRequest.builder()
                .queryParam("status", "a,2")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Utils.extractStatus(request);
        });

        assertThat(exception.getMessage()).contains("a");
    }
}
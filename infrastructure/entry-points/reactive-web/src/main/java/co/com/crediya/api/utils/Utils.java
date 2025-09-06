package co.com.crediya.api.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static co.com.crediya.model.utils.Constant.*;

public class Utils {

    public static String extractToken(ServerRequest request) {
        return request.headers()
                .header(HttpHeaders.AUTHORIZATION)
                .stream()
                .findFirst()
                .filter(auth -> auth.startsWith("Bearer "))
                .map(auth -> auth.substring(7))
                .orElse(null);
    }

    public static List<Integer> extractStatus(ServerRequest request) {
        return request.queryParam("status")
                .map(param -> Arrays.stream(param.split(","))
                        .map(String::trim)
                        .map(s -> {
                            try {
                                return Integer.parseInt(s);
                            } catch (NumberFormatException e) {
                                throw new IllegalArgumentException(ERROR_EXTRACT_STATUS + s);
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList())
                .orElse(List.of(1));
    }
}

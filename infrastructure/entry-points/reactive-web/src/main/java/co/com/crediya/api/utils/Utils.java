package co.com.crediya.api.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.server.ServerRequest;

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
}

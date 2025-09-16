package co.com.crediya.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Structured error response")
@Builder
public class ErrorResponseHandler {

    private LocalDateTime timestamp;
    private String status;
    private String error;
    private String message;
}

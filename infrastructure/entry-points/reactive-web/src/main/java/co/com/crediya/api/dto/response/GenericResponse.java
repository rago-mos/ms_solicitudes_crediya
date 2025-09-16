package co.com.crediya.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GenericResponse {

    private LocalDateTime timestamp;
    private String status;
    private String message;
}

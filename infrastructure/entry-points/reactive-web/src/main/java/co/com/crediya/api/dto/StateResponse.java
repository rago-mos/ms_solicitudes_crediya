package co.com.crediya.api.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StateResponse {

    private String name;
    private String description;
}

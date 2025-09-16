package co.com.crediya.model.application;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class StateApplication {

    private Long idApplication;
    private Integer idState;
}

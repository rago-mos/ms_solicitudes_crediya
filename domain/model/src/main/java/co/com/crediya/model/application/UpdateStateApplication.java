package co.com.crediya.model.application;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateStateApplication {

    private String idApplication;
    private Integer idState;
}

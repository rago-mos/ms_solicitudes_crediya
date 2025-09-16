package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.request.ApplicationRequest;
import co.com.crediya.model.application.StateApplication;
import org.springframework.stereotype.Component;

@Component
public class UpdateApplicationMapper {

    public StateApplication toModel(ApplicationRequest request) {

        return StateApplication.builder()
                .idApplication(request.idApplication())
                .idState(request.idState())
                .build();
    }
}

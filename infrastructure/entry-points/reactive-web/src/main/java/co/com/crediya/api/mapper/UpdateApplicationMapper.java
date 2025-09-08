package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UpdateApplicationRequest;
import co.com.crediya.model.application.UpdateStateApplication;
import org.springframework.stereotype.Component;

@Component
public class UpdateApplicationMapper {

    public UpdateStateApplication toModel(UpdateApplicationRequest request) {

        return UpdateStateApplication.builder()
                .idApplication(request.idApplication())
                .idState(request.idState())
                .build();
    }
}

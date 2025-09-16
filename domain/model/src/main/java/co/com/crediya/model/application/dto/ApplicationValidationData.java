package co.com.crediya.model.application.dto;


import co.com.crediya.model.user.UserApplication;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ApplicationValidationData {

    private Long idApplication;
    private UserApplication user;
    private List<ApplicationAprovedView> applicationsAproved;
    private ApplicationAprovedView ApplicationNew;

}

package co.com.crediya.model.application.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationData {

    private Long idApplication;
    private String identityDocument;
    private String fullName;
    private String email;
    private Integer idStatus;
    private String statusName;
    private String loanTypeName;
    List<PaymentPlan> paymentPlans;
    private Boolean isValidatedAutomatic;

}

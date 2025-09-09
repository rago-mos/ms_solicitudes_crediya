package co.com.crediya.model.application.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateApplicationView {

    private String idApplication;
    private BigDecimal amount;
    private String identityDocument;
    private String fullName;
    private String email;
    private String statusName;
    private String loanTypeName;

}

package co.com.crediya.consumer.user;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDocumentsResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private BigDecimal baseSalary;
}

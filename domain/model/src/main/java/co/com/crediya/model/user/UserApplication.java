package co.com.crediya.model.user;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class UserApplication {

    private String firstName;
    private String lastName;
    private String email;
    private String identityDocument;
    private BigDecimal baseSalary;
}

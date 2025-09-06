package co.com.crediya.r2dbc.entities;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationViewEntity {

    @Column("monto")
    private BigDecimal amount;

    @Column("plazo")
    private Integer monthTerm;

    @Column("documento_identidad")
    private String identityDocument;

    @Column("monto_mensual_solicitud")
    private BigDecimal monthAmountApprovedApplication;

    private String fullName;

    private String email;

    @Column("nombre_estado")
    private String statusName;

    @Column("tasa_interes")
    private BigDecimal interestRate;

    @Column("nombre_tipo_prestamo")
    private String loanTypeName;

    private BigDecimal baseSalary;

}

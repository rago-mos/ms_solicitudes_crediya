package co.com.crediya.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("solicitud")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApplicationEntity {

    @Id
    @Column("id_solicitud")
    private String idApplication;

    @Column("monto")
    private BigDecimal amount;

    @Column("plazo")
    private Integer term;

    @Column("documento_identidad")
    private String identityDocument;

    @Column("fk_id_estado")
    private Integer idState;

    @Column("fk_id_tipo_prestamo")
    private Integer idLoanType;

    @Column("fecha")
    private LocalDate date;
}

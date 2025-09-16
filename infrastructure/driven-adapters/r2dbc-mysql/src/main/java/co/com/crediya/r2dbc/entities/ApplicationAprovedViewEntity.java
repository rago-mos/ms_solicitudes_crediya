package co.com.crediya.r2dbc.entities;


import jakarta.persistence.Column;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApplicationAprovedViewEntity {

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "plazo")
    private Integer plazo;

    @Column(name = "tasa_interes")
    private BigDecimal tasaInteres;
}

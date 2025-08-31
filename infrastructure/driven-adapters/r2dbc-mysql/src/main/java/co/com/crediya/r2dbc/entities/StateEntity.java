package co.com.crediya.r2dbc.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("estado")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StateEntity {

    @Id
    @Column("id_estado")
    private Integer idState;

    @Column("nombre")
    private String name;

    @Column("descripcion")
    private String description;
}

package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entities.ApplicationEntity;
import co.com.crediya.r2dbc.entities.ApplicationAprovedViewEntity;
import co.com.crediya.r2dbc.entities.LoanApplicationViewEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ApplicationReactiveRepository extends ReactiveCrudRepository<ApplicationEntity, Long>, ReactiveQueryByExampleExecutor<ApplicationEntity> {


    @Query("""
            SELECT s.monto, s.plazo, s.documento_identidad, 
                (ROUND(
                        (s.monto * (
                            (((t.tasa_interes/100)/12) * POWER(1 + ((t.tasa_interes/100)/12), s.plazo)) /
                            (POWER(1 + ((t.tasa_interes/100)/12), s.plazo) - 1)
                        )), 2)
                    ) AS monto_mensual_solicitud,
            	e.nombre AS nombre_estado,
            	t.tasa_interes,
            	t.nombre AS nombre_tipo_prestamo
            FROM solicitud s
            INNER JOIN estado e ON e.id_estado = s.fk_id_estado
            INNER JOIN tipo_prestamo t ON t.id_tipo_prestamo = s.fk_id_tipo_prestamo
            WHERE s.fk_id_estado IN (:status)
            LIMIT :size OFFSET :offset
            """)
    Flux<LoanApplicationViewEntity> findLoanApplicationDetails(List<Integer> status, int size, int offset);

    @Query("""
             SELECT COUNT(*) FROM solicitud s
                             INNER JOIN estado e ON e.id_estado  = s.fk_id_estado
                             WHERE s.fk_id_estado IN (:status)
            """)
    Mono<Long> countByStatusIn(List<Integer> status);

    Mono<Boolean> existsApplicationByIdApplication(Long id);

    @Query("""
            SELECT s.monto, s.plazo, 
                   tp.tasa_interes AS tasa_interes
            FROM solicitud s
            INNER JOIN tipo_prestamo tp ON tp.id_tipo_prestamo = s.fk_id_tipo_prestamo
            WHERE s.documento_identidad = :document
            AND s.fk_id_estado = 4
            """)
    Flux<ApplicationAprovedViewEntity> findApplicationsAproved(String document);

}

package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para el log de idempotencia de copia de inventory-peps.
 * ADR-38.
 */
@Repository
public interface InvPepsCopyJobLogJpaRepository extends JpaRepository<InvPepsCopyJobLogEntity, Long> {

    Optional<InvPepsCopyJobLogEntity> findByIdProcesoAndFase(String idProceso, int fase);

    Optional<InvPepsCopyJobLogEntity> findFirstByIdProcesoOrderByFaseDesc(String idProceso);

    void deleteByIdProceso(String idProceso);
}

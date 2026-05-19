package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence;

import jakarta.transaction.Transactional;
import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence.jpa.InvPepsCopyJobLogEntity;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.output.persistence.jpa.InvPepsCopyJobLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida: persiste el log de idempotencia de copia de inventory-peps.
 * ADR-38.
 */
@Component
@RequiredArgsConstructor
public class InvPepsCopyJobLogRepositoryAdapter implements ICopyJobLogRepositoryPort {

    private final InvPepsCopyJobLogJpaRepository jpaRepository;

    @Override
    public CopyJobLog guardar(CopyJobLog log) {
        InvPepsCopyJobLogEntity entity = toEntity(log);
        InvPepsCopyJobLogEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CopyJobLog> buscarPorIdProcesoYFase(String idProceso, int fase) {
        return jpaRepository.findByIdProcesoAndFase(idProceso, fase)
                .map(this::toDomain);
    }

    @Override
    public Optional<CopyJobLog> buscarPorIdProceso(String idProceso) {
        return jpaRepository.findFirstByIdProcesoOrderByFaseDesc(idProceso)
                .map(this::toDomain);
    }

    @Override
    @Transactional
    public void eliminarPorIdProceso(String idProceso) {
        jpaRepository.deleteByIdProceso(idProceso);
    }

    private InvPepsCopyJobLogEntity toEntity(CopyJobLog log) {
        return InvPepsCopyJobLogEntity.builder()
                .idProceso(log.getIdProceso() != null ? log.getIdProceso().toString() : null)
                .fase(log.getFase())
                .modulo(log.getModulo())
                .estado(log.getEstado())
                .fechaInicio(log.getFechaInicio())
                .fechaFin(log.getFechaFin())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas() != null ? log.getEquivalenciasGeneradas() : 0)
                .errorMessage(log.getErrorMessage())
                .build();
    }

    private CopyJobLog toDomain(InvPepsCopyJobLogEntity entity) {
        return CopyJobLog.builder()
                .idProceso(entity.getIdProceso() != null ? UUID.fromString(entity.getIdProceso()) : null)
                .fase(entity.getFase())
                .modulo(entity.getModulo())
                .estado(entity.getEstado())
                .fechaInicio(entity.getFechaInicio())
                .fechaFin(entity.getFechaFin())
                .equivalenciasGeneradas(entity.getEquivalenciasGeneradas() != null ? entity.getEquivalenciasGeneradas() : 0)
                .errorMessage(entity.getErrorMessage())
                .build();
    }
}

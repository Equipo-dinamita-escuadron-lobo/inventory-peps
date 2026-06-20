package kardex.PEPS.InventoryPEPS.copy.application.output;

import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;

import java.util.Optional;

/**
 * Puerto de salida: persistencia del log de idempotencia de copia de inventory-peps.
 * ADR-38.
 */
public interface ICopyJobLogRepositoryPort {

    CopyJobLog guardar(CopyJobLog log);

    Optional<CopyJobLog> buscarPorIdProcesoYFase(String idProceso, int fase);

    Optional<CopyJobLog> buscarPorIdProceso(String idProceso);

    void eliminarPorIdProceso(String idProceso);
}

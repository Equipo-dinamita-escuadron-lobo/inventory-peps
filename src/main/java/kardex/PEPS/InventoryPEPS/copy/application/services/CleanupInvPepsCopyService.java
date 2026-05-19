package kardex.PEPS.InventoryPEPS.copy.application.services;

import kardex.PEPS.InventoryPEPS.copy.application.input.ICleanupInvPepsCopyPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio para limpiar los registros de log de un proceso de copia de inventory-peps.
 * ADR-38.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CleanupInvPepsCopyService implements ICleanupInvPepsCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public void limpiar(String idProceso) {
        log.info("Limpiando registros de copia inventory-peps para proceso {}", idProceso);
        logRepo.eliminarPorIdProceso(idProceso);
    }
}

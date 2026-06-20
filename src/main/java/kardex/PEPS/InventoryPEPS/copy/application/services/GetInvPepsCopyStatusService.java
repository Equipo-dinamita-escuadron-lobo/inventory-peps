package kardex.PEPS.InventoryPEPS.copy.application.services;

import kardex.PEPS.InventoryPEPS.copy.application.input.IGetInvPepsCopyStatusPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.domain.exceptions.DuplicateCopyJobException;
import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio para consultar el estado de un proceso de copia de inventory-peps.
 * ADR-38.
 */
@Service
@RequiredArgsConstructor
public class GetInvPepsCopyStatusService implements IGetInvPepsCopyStatusPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public CopyStatusResponseDto obtenerEstado(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new DuplicateCopyJobException(idProceso, 0));

        return CopyStatusResponseDto.builder()
                .fase(log.getFase() != null ? log.getFase() : 0)
                .estado(log.getEstado() != null ? log.getEstado().name() : "DESCONOCIDO")
                .registrosProcesados(log.getEquivalenciasGeneradas() != null ? log.getEquivalenciasGeneradas() : 0)
                .intentos(1)
                .ultimoError(log.getErrorMessage())
                .build();
    }
}

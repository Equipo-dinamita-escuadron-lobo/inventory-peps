package kardex.PEPS.InventoryPEPS.copy.application.services;

import kardex.PEPS.InventoryPEPS.copy.application.input.ICancelInvPepsCopyPort;
import kardex.PEPS.InventoryPEPS.copy.application.output.ICopyJobLogRepositoryPort;
import kardex.PEPS.InventoryPEPS.copy.domain.enums.CopyEstado;
import kardex.PEPS.InventoryPEPS.copy.domain.exceptions.DuplicateCopyJobException;
import kardex.PEPS.InventoryPEPS.copy.domain.models.CopyJobLog;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyCancelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Servicio para cancelar un proceso de copia de inventory-peps.
 * ADR-38.
 */
@Service
@RequiredArgsConstructor
public class CancelInvPepsCopyService implements ICancelInvPepsCopyPort {

    private final ICopyJobLogRepositoryPort logRepo;

    @Override
    public CopyCancelResponseDto cancelar(String idProceso) {
        CopyJobLog log = logRepo.buscarPorIdProceso(idProceso)
                .orElseThrow(() -> new DuplicateCopyJobException(idProceso, 0));

        CopyJobLog cancelado = CopyJobLog.builder()
                .idProceso(log.getIdProceso())
                .fase(log.getFase())
                .modulo(log.getModulo())
                .estado(CopyEstado.CANCELADO)
                .fechaInicio(log.getFechaInicio())
                .fechaFin(Instant.now())
                .equivalenciasGeneradas(log.getEquivalenciasGeneradas())
                .build();
        logRepo.guardar(cancelado);

        return CopyCancelResponseDto.builder()
                .estado(CopyEstado.CANCELADO.name())
                .mensaje("Proceso de copia inventory-peps cancelado exitosamente")
                .build();
    }
}

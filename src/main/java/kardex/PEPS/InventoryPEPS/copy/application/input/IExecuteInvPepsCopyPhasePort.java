package kardex.PEPS.InventoryPEPS.copy.application.input;

import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseRequestDto;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.CopyPhaseResponseDto;

/**
 * Puerto de entrada: ejecutar una fase de copia del módulo inventory-peps.
 * ADR-38, ADR-39.
 */
public interface IExecuteInvPepsCopyPhasePort {

    CopyPhaseResponseDto ejecutar(CopyPhaseRequestDto request);
}

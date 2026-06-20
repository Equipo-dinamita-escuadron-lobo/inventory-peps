package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.controller;

import jakarta.validation.Valid;
import kardex.PEPS.InventoryPEPS.copy.application.input.*;
import kardex.PEPS.InventoryPEPS.copy.domain.exceptions.DuplicateCopyJobException;
import kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST del bounded context copy en inventory-peps.
 * Expone los 4 endpoints del contrato uniforme bajo /api/kardex/peps/copy.
 * ADR-38, ADR-39.
 */
@RestController
@RequestMapping("/api/kardex/peps/copy")
@RequiredArgsConstructor
@Slf4j
public class InvPepsCopyController {

    private final IExecuteInvPepsCopyPhasePort executePort;
    private final IGetInvPepsCopyStatusPort statusPort;
    private final ICancelInvPepsCopyPort cancelPort;
    private final ICleanupInvPepsCopyPort cleanupPort;

    /**
     * POST /api/kardex/peps/copy/phase
     */
    @PostMapping("/phase")
    public ResponseEntity<CopyPhaseResponseDto> executePhase(
            @Valid @RequestBody CopyPhaseRequestDto request) {
        log.info("Ejecutando fase {} para proceso {} en inventory-peps", request.getFase(), request.getIdProceso());
        CopyPhaseResponseDto response = executePort.ejecutar(request);
        HttpStatus status = resolverHttpStatus(response.getEstado());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * GET /api/kardex/peps/copy/{idProceso}/status
     */
    @GetMapping("/{idProceso}/status")
    public ResponseEntity<CopyStatusResponseDto> getStatus(
            @PathVariable String idProceso) {
        return ResponseEntity.ok(statusPort.obtenerEstado(idProceso));
    }

    /**
     * POST /api/kardex/peps/copy/{idProceso}/cancel
     */
    @PostMapping("/{idProceso}/cancel")
    public ResponseEntity<CopyCancelResponseDto> cancel(
            @PathVariable String idProceso) {
        return ResponseEntity.ok(cancelPort.cancelar(idProceso));
    }

    /**
     * DELETE /api/kardex/peps/copy/{idProceso}/cleanup
     */
    @DeleteMapping("/{idProceso}/cleanup")
    public ResponseEntity<Void> cleanup(
            @PathVariable String idProceso) {
        cleanupPort.limpiar(idProceso);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DuplicateCopyJobException.class)
    public ResponseEntity<String> handleNotFound(DuplicateCopyJobException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    private HttpStatus resolverHttpStatus(String estado) {
        if (estado == null) return HttpStatus.INTERNAL_SERVER_ERROR;
        return switch (estado) {
            case "COMPLETADO", "COMPLETADO_CON_ADVERTENCIAS" -> HttpStatus.OK;
            case "ERROR_NO_REINTENTABLE" -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "ERROR_REINTENTABLE" -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.OK;
        };
    }
}

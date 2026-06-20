package kardex.PEPS.InventoryPEPS.copy.infrastructure.adapters.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO de request para ejecutar una fase de copia de inventory-peps.
 * Contrato uniforme ADR-38.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CopyPhaseRequestDto {

    @NotNull
    private UUID idProceso;

    @Positive
    private int fase;

    @NotBlank
    private String entOrigen;

    private String entDestino;

    @NotNull
    private Instant snapshotCorte;

    /**
     * Equivalencias generadas por participantes anteriores.
     * InvPeps usa tabla "product" para remapear idProduct en KardexEntity (pasada 1).
     * ADR-39.
     */
    private List<CopyEquivalenciaDto> equivalenciasPrev;

    /**
     * Payload de importación (modo RESTORE).
     * Contiene los datos exportados por ejecutarExportacion() de otra instancia.
     * Null en modo DUPLICATE.
     */
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private Object datosImportados;
}

package kardex.PEPS.InventoryPEPS.copy.domain.enums;

/**
 * Estados posibles de un trabajo de copia del módulo inventory-peps.
 * Replica el contrato uniforme del orquestador (ADR-38, ADR-39).
 */
public enum CopyEstado {

    INICIADO,
    EN_PROCESO,
    COMPLETADO,
    COMPLETADO_CON_ADVERTENCIAS,
    FALLIDO,
    ERROR_NO_REINTENTABLE,
    CANCELADO
}

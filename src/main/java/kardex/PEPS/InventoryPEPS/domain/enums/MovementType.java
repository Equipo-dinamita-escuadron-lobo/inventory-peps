package kardex.PEPS.InventoryPEPS.domain.enums;

/**
 * @brief Enumeration of inventory movement types
 * 
 * Defines the various types of transactions that can affect inventory levels
 * and valuation, including commercial and non-commercial movements.
 */
public enum MovementType {
    /** @brief Entry of goods through purchase */
    PURCHASE,

    /** @brief Exit of goods through sale */
    SALE, 

    /** @brief Initial inventory balance */
    OPENING_BALANCE,

    /** @brief Return of goods to supplier */
    PURCHASE_RETURN,

    /** @brief Return of goods from customer */
    SALES_RETURN,

    /** @brief Exit of goods for non-commercial reasons (e.g., damage, internal use) */
    nonCommercialExit,

    /** @brief Entry of goods for non-commercial reasons (e.g., surplus, gifts) */
    nonCommercialEntry
}

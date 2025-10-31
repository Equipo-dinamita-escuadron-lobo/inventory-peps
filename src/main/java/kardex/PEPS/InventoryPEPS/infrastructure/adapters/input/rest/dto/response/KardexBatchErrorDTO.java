package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class KardexBatchErrorDTO {
      private Long productId;
    
    private String factCode;
    
    private String errorMessage;
    
    private int recordIndex;
}

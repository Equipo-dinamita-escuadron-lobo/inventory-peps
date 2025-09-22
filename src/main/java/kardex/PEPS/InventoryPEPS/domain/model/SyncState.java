package kardex.PEPS.InventoryPEPS.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class SyncState {
     private Long id;
    
    private String syncType;
    
    private String enterpriseId;
    
    private Instant lastSyncDate;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}

package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.exception.dto;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO {
    private Integer status;
    private String message;
    private String url;
    private String method;

    public ResponseEntity<ErrorResponseDTO> of() {
        return ResponseEntity.status(this.status).body(this);
    }
}

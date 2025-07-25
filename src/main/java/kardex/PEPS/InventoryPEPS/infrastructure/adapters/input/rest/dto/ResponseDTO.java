package kardex.PEPS.InventoryPEPS.infrastructure.adapters.input.rest.dto;

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
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> {
    private T data;
    private Integer status;
    private String message;

    public ResponseEntity<ResponseDTO<T>> of() {
        return ResponseEntity.status(this.status).body(this);
    }
    
}

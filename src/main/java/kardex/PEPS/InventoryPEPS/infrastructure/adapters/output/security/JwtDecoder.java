package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.security;

import java.util.Base64;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @brief Utility for decoding JWT tokens manually
 * 
 * Provides methods to parse JWT strings and extract specific claims or the tenant ID
 * without relying on the full Spring Security context.
 */
@Component
@Slf4j
public class JwtDecoder {
    private final ObjectMapper objectMapper = new ObjectMapper();

     /**
     * @brief Extracts the tenant ID from a JWT string
     * 
     * Decodes the payload and retrieves the "sub" claim which represents the tenant/user ID.
     * 
     * @param jwtToken The raw JWT string (with or without "Bearer " prefix)
     * @return The extracted tenant ID, or null if invalid/not found
    */
    public String extractTenantId(String jwtToken){
        try {
            // Remover el prefijo "Bearer " si existe
            String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
            
            // Un JWT tiene 3 partes separadas por puntos: header.payload.signature
            String[] chunks = token.split("\\.");
            
            if (chunks.length != 3) {
                log.error("Token JWT inválido: no tiene el formato correcto");
                return null;
            }
            
            // Decodificar el payload (segunda parte)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            
            // Parsear el JSON del payload
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Extraer el claim "sub" que contiene el tenant ID
            JsonNode subNode = jsonNode.get("sub");
            if (subNode != null) {
                String tenantId = subNode.asText();
                log.debug("Tenant ID extraído del JWT: {}", tenantId);
                return tenantId;
            } else {
                log.warn("No se encontró el claim 'sub' en el token JWT");
                return null;
            }
            
            
        } catch (Exception e) {
           log.error("Error al decodificar el token JWT: {}", e.getMessage(), e);
            return null;
        }
    }

     /**
     * @brief Extracts a specific claim from a JWT string
     * 
     * @param jwtToken The raw JWT string
     * @param claimName The name of the claim to extract
     * @return The claim value as string, or null if invalid/not found
    */
    public String extractClaim(String jwtToken, String claimName) {
        try{
            // Remover el prefijo "Bearer " si existe
            String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
            
            // Un JWT tiene 3 partes separadas por puntos: header.payload.signature
            String[] chunks = token.split("\\.");
            
            if (chunks.length != 3) {
                log.error("Token JWT inválido: no tiene el formato correcto");
                return null;
            }
            
            // Decodificar el payload (segunda parte)
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            
            // Parsear el JSON del payload
            JsonNode jsonNode = objectMapper.readTree(payload);
            
            // Extraer el claim solicitado
            JsonNode claimNode = jsonNode.get(claimName);
            if (claimNode != null) {
                String claimValue = claimNode.asText();
                log.debug("Claim '{}' extraído del JWT: {}", claimName, claimValue);
                return claimValue;
            } else {
                log.warn("No se encontró el claim '{}' en el token JWT", claimName);
                return null;
            }

        }catch(Exception e){
            log.error("Error al extraer el claim '{}' del token JWT: {}", claimName, e.getMessage(), e);
            return null;
        }
    }


}

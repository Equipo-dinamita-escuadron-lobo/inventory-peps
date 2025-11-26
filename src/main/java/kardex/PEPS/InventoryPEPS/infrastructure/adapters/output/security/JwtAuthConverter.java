package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.security;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;

/**
 * @brief Converter for JWT to Authentication Token
 * 
 * Converts a Spring Security JWT object into an AbstractAuthenticationToken.
 * Extracts roles and authorities from the JWT claims.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt,AbstractAuthenticationToken>, IJwtUtils {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter=new JwtGrantedAuthoritiesConverter();
    
    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAtrribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    Jwt jwtToken;

     /**
     * @brief Converts a JWT into an Authentication Token
     * 
     * Extracts authorities and resource roles to create a JwtAuthenticationToken.
     *
     * @param jwt The JWT to convert
     * @return The resulting AbstractAuthenticationToken
     */
     @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
      Collection<GrantedAuthority> authorities=Stream
            .concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(),extractResourceRoles(jwt).stream())
            .toList();
             this.jwtToken = jwt;
        return new JwtAuthenticationToken(jwt, authorities, getPrincipleName(jwt)); 
    }


    /**
     * @brief Retrieves the principal name from the JWT
     *
     * Uses the configured principle attribute or defaults to the "sub" claim.
     *
     * @param jwt The JWT to extract the name from
     * @return The principal name
    */
    private String getPrincipleName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;

        if (principleAtrribute != null) {
            claimName = principleAtrribute;
        }

        return jwt.getClaim(claimName);
    }

    /**
     * @brief Extracts resource roles from the JWT
     * 
     * Looks for "resource_access" claim and extracts roles for the specific resource ID.
     * Maps them to GrantedAuthority objects with "ROLE_" prefix.
     * 
     * @param jwt The JWT to extract roles from
     * @return Collection of GrantedAuthority objects
    */
    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim("resource_access") == null) {
            return List.of();
        }

        resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess.get(resourceId) == null) {
            return List.of();
        }

        resource = (Map<String, Object>) resourceAccess.get(resourceId);

        if (resource.get("roles") == null) {
            return List.of();
        }

        resourceRoles = (Collection<String>) resource.get("roles");

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role)))
                .toList();
    }
   
    /**
     * @brief Retrieves the user ID (subject) from the current token
     * 
     * @return The user identifier
    */
    @Override
    public String getId() {
        return (String) jwtToken.getClaims().get("sub");
    }

    /**
     * @brief Retrieves the raw token value
     * 
     * @return The JWT token string
    */
    @Override
    public String getToken() {
       return jwtToken.getTokenValue();
    }

   
    
}

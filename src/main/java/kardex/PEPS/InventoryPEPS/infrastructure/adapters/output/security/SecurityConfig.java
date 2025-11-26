package kardex.PEPS.InventoryPEPS.infrastructure.adapters.output.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

/**
 * @brief Security Configuration
 * 
 * Configures Spring Security filter chains, OAuth2 resource server settings,
 * and session management policies.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    @Autowired
    private JwtAuthConverter jwtAuthConverter;

    /**
     * @brief Configures the security filter chain
     * 
     * Disables CSRF, configures public endpoints (Swagger, Actuator),
     * requires authentication for other requests, sets up OAuth2 resource server
     * with JWT converter, and enforces stateless session policy.
     * 
     * @param httpSecurity The HttpSecurity object to configure
     * @return The configured SecurityFilterChain
     * @throws Exception If configuration fails
    */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(http -> http
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/actuator/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth -> {
                    oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter));
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}

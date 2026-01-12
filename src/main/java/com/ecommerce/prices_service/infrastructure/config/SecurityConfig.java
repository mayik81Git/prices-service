package com.ecommerce.prices_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

/**
 * <h2>SecurityConfig</h2>
 * Configuración de seguridad perimetral del microservicio.
 * <p>
 * Establece una política de autenticación basada en Bearer Tokens (JWT).
 * En entornos locales, utiliza un decodificador con clave simétrica (HMAC),
 * mientras que en producción se delega la validación al proveedor de identidad (IdP).
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Clave secreta para validación de tokens en entorno local/desarrollo
    private final String MOCK_SECRET = "esta-es-una-clave-secreta-de-32-caracteres-minimo";

    /**
     * Define la cadena de filtros de seguridad.
     *
     * @param http Configuración de seguridad web.
     * @return Filtro de seguridad configurado.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitado para APIs REST stateless
                .authorizeHttpRequests(auth -> auth
                        // Acceso libre para documentación técnica y monitorización
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health/**", "/h2-console/**").permitAll()
                        // El resto de peticiones requieren autenticación JWT
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * Decodificador de JWT para validación local.
     * <p>
     * En producción, este Bean se configura mediante 'spring.security.oauth2.resourceserver.jwt.issuer-uri'
     * en el archivo application-prod.yml.
     * </p>
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(MOCK_SECRET.getBytes(), "HmacSHA256")
        ).build();
    }
}

package com.ecommerce.prices_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    // En producción esto vendría de un Secret de Kubernetes o Vault
    private final String JWT_SECRET = "esta-es-una-clave-secreta-de-32-caracteres-minimo";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // Política Stateless: No guardamos sesión, cada petición debe traer su token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Públicos: Salud del sistema y documentación
                        .requestMatchers("/actuator/health/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Protegidos: La API de precios y las métricas detalladas (Prometheus)
                        .requestMatchers("/api/v1/prices/**").authenticated()
                        .requestMatchers("/actuator/prometheus").authenticated()
                        // El resto, por seguridad, siempre autenticado
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(
                new SecretKeySpec(JWT_SECRET.getBytes(), "HmacSHA256")
        ).build();
    }
}

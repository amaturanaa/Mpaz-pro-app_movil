package com.example.api_gateway.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Flux;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] strings = {
            "/asignaturas/**", "/unidades/**", "/objetivos/**", "/contenidos/**"
    };
    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String secret;

    // Rutas del microservicio de contenidos
    private static final String[] RUTAS_CONTENIDOS = strings;
    // Rutas del microservicio de actividades
    private static final String[] RUTAS_ACTIVIDADES = {
            "/desafios/**", "/pruebas/**", "/preguntas/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/admin/**").hasRole("UTP")
                // Consultar contenidos: cualquier perfil
                .pathMatchers(HttpMethod.GET, RUTAS_CONTENIDOS)
                    .hasAnyRole("UTP", "DOCENTE", "ESTUDIANTE")
                // Crear, editar o borrar contenidos: solo UTP
                .pathMatchers(RUTAS_CONTENIDOS).hasRole("UTP")
                    // Consultar actividades: cualquier perfil
                    .pathMatchers(HttpMethod.GET, RUTAS_ACTIVIDADES)
                    .hasAnyRole("UTP", "DOCENTE", "ESTUDIANTE")
                    // Crear, editar o borrar actividades: solo UTP
                    .pathMatchers(RUTAS_ACTIVIDADES).hasRole("UTP")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorDeRoles()))
            )
            .build();
    }

    // Lee el claim "role" del token y lo convierte en ROLE_UTP, ROLE_DOCENTE, etc.
    private ReactiveJwtAuthenticationConverter convertidorDeRoles() {
        ReactiveJwtAuthenticationConverter convertidor = new ReactiveJwtAuthenticationConverter();
        convertidor.setJwtGrantedAuthoritiesConverter(jwt -> {
            String rol = jwt.getClaimAsString("role");
            if (rol == null || rol.isBlank()) {
                return Flux.<GrantedAuthority>empty();
            }
            return Flux.<GrantedAuthority>just(new SimpleGrantedAuthority("ROLE_" + rol));
        });
        return convertidor;
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }
}
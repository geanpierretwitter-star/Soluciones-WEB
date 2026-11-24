
package com.Meditrack.Meditrack.Security;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtFiltro jwtFiltro;

    public SecurityConfig(JwtFiltro jwtFiltro) {
        this.jwtFiltro = jwtFiltro;
    }

    // CONFIGURACION CORS PARA ANGULAR
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // EVITAR REGISTRAR EL FILTRO JWT DOS VECES
    @Bean
    public FilterRegistrationBean<JwtFiltro> jwtFiltroRegistration() {

        FilterRegistrationBean<JwtFiltro> registration =
                new FilterRegistrationBean<>(jwtFiltro);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // HABILITAR CORS
            .cors(cors ->
                    cors.configurationSource(corsConfigurationSource())
            )

            // DESACTIVAR CSRF
            .csrf(csrf -> csrf.disable())

            // SESIONES SIN ESTADO
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // MANEJO DE ERRORES DE SEGURIDAD
            .exceptionHandling(exceptions -> exceptions

                    // ERROR 401
                    .authenticationEntryPoint((request, response, exception) -> {

                        System.err.println(
                                "SECURITY DEBUG - AuthenticationEntryPoint: "
                                        + request.getRequestURI()
                        );

                        response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "Se requiere una autenticación válida."
                        );
                    })

                    // ERROR 403
                    .accessDeniedHandler((request, response, exception) -> {

                        System.err.println(
                                "SECURITY DEBUG - AccessDeniedHandler: "
                                        + request.getRequestURI()
                                        + " | " + exception.getMessage()
                        );

                        response.sendError(
                                HttpServletResponse.SC_FORBIDDEN,
                                "No tienes permisos para acceder a este recurso."
                        );
                    })
            )

            // REGLAS DE AUTORIZACION
            .authorizeHttpRequests(auth -> auth

                    // PERMITIR DESPACHO INTERNO DE ERRORES
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                    // LOGIN PUBLICO
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/auth/login"
                    ).permitAll()

                    // REGISTRO PUBLICO DE PACIENTES
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/usuarios/registro/paciente"
                    ).permitAll()

                    // REGISTRO PUBLICO DE FAMILIARES
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/usuarios/registro/familiar"
                    ).permitAll()

                    // REGISTRO DE MEDICOS - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/usuarios/admin/medicos"
                    ).hasRole("ADMIN")

                    // REGISTRO DE ENFERMERAS - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/usuarios/admin/enfermeras"
                    ).hasRole("ADMIN")

                    // LISTAR PERSONAL CLINICO - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/usuarios/admin/personal-clinico"
                    ).hasRole("ADMIN")

                    // LISTAR ESPECIALIDADES - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/especialidades"
                    ).hasRole("ADMIN")

                    // ACTIVAR O DESACTIVAR PERSONAL CLINICO - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/usuarios/admin/personal-clinico/**"
                    ).hasRole("ADMIN")

                    // REGISTRAR HORARIOS MEDICOS - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/horarios-medicos/**"
                    ).hasRole("ADMIN")

                    // ACTIVAR O DESACTIVAR HORARIOS - SOLO ADMIN
                    .requestMatchers(
                            HttpMethod.PATCH,
                            "/api/horarios-medicos/**"
                    ).hasRole("ADMIN")

                    // PANEL ADMINISTRATIVO - SOLO ADMIN
                    .requestMatchers(
                            "/api/admin/**"
                    ).hasRole("ADMIN")

                    // LAS DEMAS RUTAS REQUIEREN AUTENTICACION
                    .anyRequest().authenticated()
            )

            // DESACTIVAR AUTENTICACION BASICA Y FORMULARIO
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())

            // AGREGAR FILTRO JWT
            .addFilterBefore(
                    jwtFiltro,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}

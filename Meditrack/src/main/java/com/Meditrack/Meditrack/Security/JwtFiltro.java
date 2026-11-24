
package com.Meditrack.Meditrack.Security;

import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Repositorio.UsuarioRepositorio;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    public JwtFiltro(
            JwtServicio jwtServicio,
            UsuarioRepositorio usuarioRepositorio) {
        this.jwtServicio = jwtServicio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String encabezado = request.getHeader("Authorization");

        if (encabezado == null
                || !encabezado.startsWith("Bearer ")
                || encabezado.length() <= 7) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = encabezado.substring(7);

        try {
            Claims claims = jwtServicio.validarToken(token);

            String idUsuario = claims.getSubject();
            String rolToken = claims.get("rol", String.class);

            if (idUsuario == null || rolToken == null
                    || rolToken.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token inválido: faltan datos del usuario.");
                return;
            }

            if (SecurityContextHolder.getContext()
                    .getAuthentication() == null) {

                Long id;

                try {
                    id = Long.valueOf(idUsuario);
                } catch (NumberFormatException e) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Token inválido: ID de usuario incorrecto.");
                    return;
                }

                Usuario usuario = usuarioRepositorio.findById(id)
                        .orElse(null);

                if (usuario == null
                        || !"Activo".equalsIgnoreCase(
                                usuario.getEstado())) {

                    SecurityContextHolder.clearContext();
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "La cuenta no existe o está inactiva.");
                    return;
                }

                // El rol vigente se obtiene de MySQL, no del token.
                String rolActual = usuario.getRol();

                UsernamePasswordAuthenticationToken autenticacion =
                        new UsernamePasswordAuthenticationToken(
                                idUsuario,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + rolActual)
                                )
                        );

                autenticacion.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(autenticacion);
            }

        } catch (JwtException | IllegalArgumentException e) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Token inválido o expirado.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

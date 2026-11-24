
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.DTO.LoginRequest;
import com.Meditrack.Meditrack.DTO.LoginResponse;
import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Repositorio.UsuarioRepositorio;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public AutenticacionServicio(
            UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }
        @Transactional(readOnly = true)
      public Usuario validarCredenciales(LoginRequest request) {

        if (request == null
                || request.getCorreo() == null
                || request.getPassword() == null
                || request.getCorreo().isBlank()
                || request.getPassword().isBlank()) {

            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos.");
        }

        String correo = request.getCorreo()
        .trim()
        .toLowerCase();

Usuario usuario = usuarioRepositorio
        .findByCorreo(correo)
        .orElseThrow(() -> new BadCredentialsException(
                "Correo o contraseña incorrectos."));
        if (!passwordEncoder.matches(
                request.getPassword(),
                usuario.getPasswordHash())) {

            throw new BadCredentialsException(
                    "Correo o contraseña incorrectos.");
        }

        if (!"Activo".equalsIgnoreCase(usuario.getEstado())) {
            throw new BadCredentialsException(
                    "La cuenta está inactiva.");
        }

        return usuario;
    }
}

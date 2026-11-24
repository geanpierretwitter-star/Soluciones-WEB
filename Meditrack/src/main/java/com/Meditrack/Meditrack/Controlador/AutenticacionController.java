
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.DTO.LoginRequest;
import com.Meditrack.Meditrack.DTO.LoginResponse;
import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Security.JwtServicio;
import com.Meditrack.Meditrack.Servicio.AutenticacionServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

    private final AutenticacionServicio autenticacionServicio;
    private final JwtServicio jwtServicio;

    public AutenticacionController(
            AutenticacionServicio autenticacionServicio,
            JwtServicio jwtServicio) {

        this.autenticacionServicio = autenticacionServicio;
        this.jwtServicio = jwtServicio;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(
            @RequestBody LoginRequest request) {

        Usuario usuario =
                autenticacionServicio.validarCredenciales(request);

        String token = jwtServicio.generarToken(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getRol()
        );

        LoginResponse respuesta = new LoginResponse(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getRol()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(respuesta);
    }
}


package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.DTO.RegistroPacienteRequest;
import com.Meditrack.Meditrack.DTO.RegistroMedicoRequest;
import com.Meditrack.Meditrack.DTO.RegistroEnfermeraRequest;
import com.Meditrack.Meditrack.DTO.UsuarioRespuestaDTO;

import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Servicio.UsuarioServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioServicio usuarioServicio;

    public UsuarioController(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // BUSCAR USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> buscarPorId(
            @PathVariable Long id) {

        Usuario usuario = usuarioServicio.buscarPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioRespuestaDTO respuesta = new UsuarioRespuestaDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado()
        );

        return ResponseEntity.ok(respuesta);
    }

    // REGISTRO PUBLICO DE PACIENTES
    @PostMapping("/registro/paciente")
    public ResponseEntity<UsuarioRespuestaDTO> registrarPaciente(
            @RequestBody RegistroPacienteRequest request) {

        UsuarioRespuestaDTO usuario =
                usuarioServicio.registrarPaciente(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    // REGISTRO PUBLICO DE FAMILIARES
    @PostMapping("/registro/familiar")
    public ResponseEntity<UsuarioRespuestaDTO> registrarFamiliar(
            @RequestBody RegistroPacienteRequest request) {

        UsuarioRespuestaDTO usuario =
                usuarioServicio.registrarFamiliar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }

    // REGISTRO DE MEDICOS - SOLO ADMINISTRADOR
    @PostMapping("/admin/medicos")
    public ResponseEntity<?> registrarMedico(
            @RequestBody RegistroMedicoRequest request) {

        try {

            UsuarioRespuestaDTO respuesta =
                    usuarioServicio.registrarMedico(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // REGISTRO DE ENFERMERAS - SOLO ADMINISTRADOR
    @PostMapping("/admin/enfermeras")
    public ResponseEntity<?> registrarEnfermera(
            @RequestBody RegistroEnfermeraRequest request) {

        try {

            UsuarioRespuestaDTO respuesta =
                    usuarioServicio.registrarEnfermera(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // LISTAR PERSONAL CLINICO - SOLO ADMINISTRADOR
    @GetMapping("/admin/personal-clinico")
    public ResponseEntity<List<UsuarioRespuestaDTO>> listarPersonalClinico() {

        List<UsuarioRespuestaDTO> personal =
                usuarioServicio.listarPersonalClinico();

        return ResponseEntity.ok(personal);
    }

    // ACTIVAR O DESACTIVAR PERSONAL CLINICO - SOLO ADMINISTRADOR
    @PatchMapping("/admin/personal-clinico/{id}/estado")
    public ResponseEntity<?> cambiarEstadoPersonalClinico(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {

            String estado = request.get("estado");

            UsuarioRespuestaDTO respuesta =
                    usuarioServicio.cambiarEstadoPersonalClinico(id, estado);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}

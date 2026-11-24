
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.DTO.RegistroMedicoRequest;
import com.Meditrack.Meditrack.DTO.RegistroEnfermeraRequest;
import com.Meditrack.Meditrack.DTO.UsuarioRespuestaDTO;
import com.Meditrack.Meditrack.DTO.CambiarEstadoRequest;
import com.Meditrack.Meditrack.Servicio.UsuarioServicio;
import com.Meditrack.Meditrack.DTO.PersonalClinicoDetalleDTO;
import com.Meditrack.Meditrack.DTO.ActualizarPersonalClinicoRequest;
import com.Meditrack.Meditrack.DTO.UsuarioAdminDetalleDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioServicio usuarioServicio;

    public AdminController(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    // Inicio del panel administrativo
    @GetMapping("/inicio")
    public Map<String, String> inicio() {
        return Map.of(
                "mensaje",
                "Bienvenido al panel de administración de MediTrack",
                "rol",
                "ADMIN"
        );
    }

    // Listar médicos y enfermeras
    @GetMapping("/personal-clinico")
    public ResponseEntity<List<UsuarioRespuestaDTO>>
            listarPersonalClinico() {

        List<UsuarioRespuestaDTO> personal =
                usuarioServicio.listarPersonalClinico();

        return ResponseEntity.ok(personal);
    }

    // Registrar un médico
    @PostMapping("/medicos")
    public ResponseEntity<UsuarioRespuestaDTO> registrarMedico(
            @RequestBody RegistroMedicoRequest request) {

        UsuarioRespuestaDTO respuesta =
                usuarioServicio.registrarMedico(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // Registrar una enfermera
    @PostMapping("/enfermeras")
    public ResponseEntity<UsuarioRespuestaDTO> registrarEnfermera(
            @RequestBody RegistroEnfermeraRequest request) {

        UsuarioRespuestaDTO respuesta =
                usuarioServicio.registrarEnfermera(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(respuesta);
    }

    // Activar o desactivar un médico o una enfermera
    @PatchMapping("/personal-clinico/{id}/estado")
    public ResponseEntity<UsuarioRespuestaDTO> cambiarEstadoPersonalClinico(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        if (request == null || request.getEstado() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el estado del personal clínico.");
        }

        UsuarioRespuestaDTO respuesta =
                usuarioServicio.cambiarEstadoPersonalClinico(
                        id, request.getEstado());

        return ResponseEntity.ok(respuesta);
    }
// LISTAR PACIENTES Y FAMILIARES
@GetMapping("/usuarios")
public ResponseEntity<List<UsuarioRespuestaDTO>> listarUsuarios() {

    List<UsuarioRespuestaDTO> usuarios =
            usuarioServicio.listarPacientesYFamiliares();

    return ResponseEntity.ok(usuarios);
}
// ============================================
// CONSULTAR FAMILIARES VINCULADOS A UN PACIENTE
// ============================================

@GetMapping("/usuarios/{id}/familiares")
public ResponseEntity<?> listarFamiliaresVinculados(
        @PathVariable Long id) {

    try {
        return ResponseEntity.ok(
                usuarioServicio.listarFamiliaresVinculadosPaciente(id)
        );

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", e.getMessage()));
    }
}
// ============================================
// CONSULTAR PACIENTES VINCULADOS A UN FAMILIAR
// ============================================

@GetMapping("/usuarios/{id}/pacientes")
public ResponseEntity<?> listarPacientesVinculados(
        @PathVariable Long id) {

    try {
        return ResponseEntity.ok(
                usuarioServicio.listarPacientesVinculadosFamiliar(id)
        );

    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", e.getMessage()));
    }
}

    // ============================================
    // CONSULTAR DETALLES DE PACIENTES Y FAMILIARES
    // ============================================

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<?> buscarDetalleUsuarioAdmin(
            @PathVariable Long id) {

        try {
            UsuarioAdminDetalleDTO respuesta =
                    usuarioServicio.buscarDetalleUsuarioAdmin(id);

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ============================================
    // ACTIVAR O DESACTIVAR PACIENTES Y FAMILIARES
    // ============================================

    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuarioAdmin(
            @PathVariable Long id,
            @RequestBody CambiarEstadoRequest request) {

        try {

            if (request == null || request.getEstado() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "mensaje",
                                "Debe indicar el estado del usuario."
                        ));
            }

            UsuarioRespuestaDTO respuesta =
                    usuarioServicio.cambiarEstadoUsuarioAdmin(
                            id, request.getEstado());

            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }


    // ============================================
    // CONSULTAR DETALLES DEL PERSONAL CLÍNICO
    // ============================================

    @GetMapping("/personal-clinico/{id}")
    public ResponseEntity<PersonalClinicoDetalleDTO> buscarDetallePersonalClinico(
            @PathVariable Long id) {

        PersonalClinicoDetalleDTO respuesta =
                usuarioServicio.buscarDetallePersonalClinico(id);

        return ResponseEntity.ok(respuesta);
    }


    // ============================================
    // ACTUALIZAR PERSONAL CLÍNICO
    // ============================================

    @PutMapping("/personal-clinico/{id}")
    public ResponseEntity<UsuarioRespuestaDTO> actualizarPersonalClinico(
            @PathVariable Long id,
            @RequestBody ActualizarPersonalClinicoRequest request) {

        UsuarioRespuestaDTO respuesta =
                usuarioServicio.actualizarPersonalClinico(id, request);

        return ResponseEntity.ok(respuesta);
    }

// ============================================
// BAJA LÓGICA DEL PERSONAL CLÍNICO
// ============================================

@DeleteMapping("/personal-clinico/{id}")
public ResponseEntity<Map<String, String>> eliminarPersonalClinico(
        @PathVariable Long id) {

    usuarioServicio.cambiarEstadoPersonalClinico(id, "Inactivo");

    return ResponseEntity.ok(
            Map.of("mensaje", "Personal clínico dado de baja correctamente.")
    );
}
}

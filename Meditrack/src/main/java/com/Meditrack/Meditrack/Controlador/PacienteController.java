
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.Entidad.CodigoVinculacion;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;
import com.Meditrack.Meditrack.Servicio.PacienteServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteServicio pacienteServicio;

    public PacienteController(PacienteServicio pacienteServicio) {
        this.pacienteServicio = pacienteServicio;
    }

    // GENERAR CODIGO DE VINCULACION
    @PostMapping("/codigo-vinculacion")
    public ResponseEntity<?> generarCodigo(
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Debes iniciar sesión."));
        }

        try {
            Long idUsuario = Long.valueOf(authentication.getName());

            CodigoVinculacion codigo =
                    pacienteServicio.generarCodigoVinculacion(idUsuario);

            return ResponseEntity.ok(Map.of(
                    "codigo", codigo.getCodigo(),
                    "fechaExpiracion", codigo.getFechaExpiracion(),
                    "mensaje", "Código de vinculación generado correctamente."
            ));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "mensaje",
                            "No se pudo identificar al usuario autenticado."
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // LISTAR FAMILIARES VINCULADOS AL PACIENTE
    @GetMapping("/mis-familiares")
    public ResponseEntity<?> listarFamiliares(
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Debes iniciar sesión."));
        }

        try {
            Long idUsuario = Long.valueOf(authentication.getName());

            List<PacienteFamiliar> familiares =
                    pacienteServicio.listarFamiliaresVinculados(idUsuario);

            return ResponseEntity.ok(familiares);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "mensaje",
                            "No se pudo identificar al usuario autenticado."
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // AUTORIZAR O REVOCAR PERMISO DE GESTION DE CITAS
    @PutMapping("/familiares/{idFamilia}/autorizacion")
    public ResponseEntity<?> cambiarAutorizacion(
            @PathVariable Long idFamilia,
            @RequestBody Map<String, Boolean> solicitud,
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Debes iniciar sesión."));
        }

        try {
            Long idUsuario = Long.valueOf(authentication.getName());

            Boolean autorizado = solicitud.get("autorizadoCitas");

            PacienteFamiliar relacion =
                    pacienteServicio.cambiarAutorizacionFamiliar(
                            idUsuario,
                            idFamilia,
                            autorizado
                    );

            return ResponseEntity.ok(Map.of(
                    "idPaciente", relacion.getIdPaciente(),
                    "idFamilia", relacion.getIdFamilia(),
                    "autorizadoCitas", relacion.getAutorizadoCitas(),
                    "mensaje", Boolean.TRUE.equals(autorizado)
                            ? "Permiso de gestión de citas autorizado correctamente."
                            : "Permiso de gestión de citas revocado correctamente."
            ));

        } catch (NumberFormatException e) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "mensaje",
                            "No se pudo identificar al usuario autenticado."
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}

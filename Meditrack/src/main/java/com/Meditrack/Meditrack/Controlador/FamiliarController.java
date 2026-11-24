
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.DTO.PacienteVinculadoDTO;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;
import com.Meditrack.Meditrack.Servicio.FamiliarServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/familiares")
public class FamiliarController {

    private final FamiliarServicio familiarServicio;

    public FamiliarController(FamiliarServicio familiarServicio) {
        this.familiarServicio = familiarServicio;
    }

    // VINCULAR FAMILIAR CON PACIENTE
    @PostMapping("/vincular")
    public ResponseEntity<?> vincular(
            @RequestBody SolicitudVinculacion solicitud,
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Debes iniciar sesión."));
        }

        try {
            Long idUsuario = Long.valueOf(authentication.getName());

            PacienteFamiliar relacion =
                    familiarServicio.vincularConCodigo(
                            idUsuario,
                            solicitud.getCodigo(),
                            solicitud.getParentesco()
                    );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Vinculación realizada correctamente.",
                    "idPaciente", relacion.getIdPaciente(),
                    "idFamilia", relacion.getIdFamilia(),
                    "parentesco", relacion.getParentesco(),
                    "autorizadoCitas", relacion.getAutorizadoCitas()
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

    // LISTAR PACIENTES VINCULADOS AL FAMILIAR
    @GetMapping("/mis-pacientes")
    public ResponseEntity<?> listarMisPacientes(
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("mensaje", "Debes iniciar sesión."));
        }

        try {
            Long idUsuario = Long.valueOf(authentication.getName());

            List<PacienteVinculadoDTO> pacientes =
                    familiarServicio.listarPacientesVinculados(idUsuario);

            return ResponseEntity.ok(pacientes);

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

    // DTO PARA SOLICITAR VINCULACIÓN
    public static class SolicitudVinculacion {

        private String codigo;
        private String parentesco;

        public SolicitudVinculacion() {
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getParentesco() {
            return parentesco;
        }

        public void setParentesco(String parentesco) {
            this.parentesco = parentesco;
        }
    }
}


package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.Entidad.Cita;
import com.Meditrack.Meditrack.Servicio.CitaServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaServicio citaServicio;

    public CitaController(CitaServicio citaServicio) {
        this.citaServicio = citaServicio;
    }

    // REGISTRAR CITA MEDICA
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCita(
            @RequestBody Map<String, Object> solicitud,
            Authentication authentication) {

        // VALIDAR AUTENTICACION
        if (authentication == null
                || authentication.getName() == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "Debes iniciar sesión."
                    ));
        }

        try {

            // OBTENER ID DEL USUARIO AUTENTICADO
            Long idUsuario = Long.valueOf(
                    authentication.getName()
            );

            // VALIDAR DATOS OBLIGATORIOS
            if (solicitud.get("idMedico") == null
                    || solicitud.get("fechaHora") == null) {

                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "mensaje",
                                "El médico y la fecha de la cita son obligatorios."
                        ));
            }

            // OBTENER ID DEL MEDICO
            Long idMedico = Long.valueOf(
                    solicitud.get("idMedico").toString()
            );

            // OBTENER FECHA Y HORA
            LocalDateTime fechaHora = LocalDateTime.parse(
                    solicitud.get("fechaHora").toString()
            );

            // OBTENER MOTIVO
            String motivo = solicitud.get("motivo") == null
                    ? null
                    : solicitud.get("motivo").toString();

            // REGISTRAR CITA
            Cita cita = citaServicio.registrarCita(
                    idUsuario,
                    idMedico,
                    fechaHora,
                    motivo
            );

            // RESPUESTA EXITOSA
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "idCita", cita.getIdCita(),
                            "idMedico", cita.getMedico().getIdMedico(),
                            "fechaHora", cita.getFechaHora(),
                            "motivo", cita.getMotivo() == null
                                    ? ""
                                    : cita.getMotivo(),
                            "estado", cita.getEstado(),
                            "mensaje",
                            "Cita médica registrada correctamente."
                    ));

        } catch (NumberFormatException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            "El identificador del médico no es válido."
                    ));

        } catch (DateTimeParseException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            "El formato de fecha y hora no es válido. Usa yyyy-MM-ddTHH:mm:ss."
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }
    
    // LISTAR CITAS DEL PACIENTE AUTENTICADO
    @GetMapping("/mis-citas")
    public ResponseEntity<?> listarMisCitas(
            Authentication authentication) {

        // VALIDAR AUTENTICACION
        if (authentication == null
                || authentication.getName() == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "Debes iniciar sesión."
                    ));
        }

        try {

            // OBTENER ID DEL USUARIO AUTENTICADO
            Long idUsuario = Long.valueOf(
                    authentication.getName()
            );

            // CONSULTAR CITAS DEL PACIENTE
            var citas = citaServicio.listarCitasPaciente(idUsuario);

            return ResponseEntity.ok(citas);

        } catch (NumberFormatException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            "El identificador del usuario no es válido."
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }
    
    // LISTAR CITAS DEL MEDICO AUTENTICADO
    @GetMapping("/medico/mis-citas")
    public ResponseEntity<?> listarMisCitasMedico(
            Authentication authentication) {

        // VALIDAR AUTENTICACION
        if (authentication == null
                || authentication.getName() == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "Debes iniciar sesión."
                    ));
        }

        try {

            // OBTENER ID DEL USUARIO AUTENTICADO
            Long idUsuario = Long.valueOf(
                    authentication.getName()
            );

            // CONSULTAR CITAS DEL MEDICO
            var citas = citaServicio.listarCitasMedico(idUsuario);

            return ResponseEntity.ok(citas);

        } catch (NumberFormatException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            "El identificador del usuario no es válido."
                    ));

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            e.getMessage()
                    ));
        }
    }
    // CANCELAR CITA DEL PACIENTE AUTENTICADO
@PatchMapping("/{idCita}/cancelar")
public ResponseEntity<?> cancelarCita(
        @PathVariable Long idCita,
        Authentication authentication) {

    // VALIDAR AUTENTICACION
    if (authentication == null
            || authentication.getName() == null) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensaje",
                        "Debes iniciar sesión."
                ));
    }

    try {

        // OBTENER ID DEL USUARIO AUTENTICADO
        Long idUsuario = Long.valueOf(
                authentication.getName()
        );

        // CANCELAR CITA
        Cita cita = citaServicio.cancelarCitaPaciente(
                idUsuario,
                idCita
        );

        // RESPUESTA EXITOSA
        return ResponseEntity.ok(
                Map.of(
                        "idCita", cita.getIdCita(),
                        "estado", cita.getEstado(),
                        "mensaje", "Cita cancelada correctamente."
                )
        );

    } catch (NumberFormatException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        "El identificador del usuario no es válido."
                ));

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        e.getMessage()
                ));
    }
}
// CANCELAR CITA DEL MEDICO AUTENTICADO
@PatchMapping("/medico/{idCita}/cancelar")
public ResponseEntity<?> cancelarCitaMedico(
        @PathVariable Long idCita,
        Authentication authentication) {

    if (authentication == null
            || authentication.getName() == null) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "mensaje",
                        "Debes iniciar sesión."
                ));
    }

    try {

        Long idUsuario = Long.valueOf(
                authentication.getName()
        );

        Cita cita = citaServicio.cancelarCitaMedico(
                idUsuario,
                idCita
        );

        return ResponseEntity.ok(
                Map.of(
                        "idCita", cita.getIdCita(),
                        "estado", cita.getEstado(),
                        "mensaje", "Cita cancelada correctamente."
                )
        );

    } catch (NumberFormatException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        "El identificador del usuario no es válido."
                ));

    } catch (IllegalArgumentException e) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje",
                        e.getMessage()
                ));
    }
}

}

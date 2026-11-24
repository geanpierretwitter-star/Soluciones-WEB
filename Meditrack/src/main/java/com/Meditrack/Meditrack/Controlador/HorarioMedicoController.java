
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.Entidad.HorarioMedico;
import com.Meditrack.Meditrack.Servicio.HorarioMedicoServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/horarios-medicos")
public class HorarioMedicoController {

    private final HorarioMedicoServicio horarioMedicoServicio;

    public HorarioMedicoController(
            HorarioMedicoServicio horarioMedicoServicio) {

        this.horarioMedicoServicio = horarioMedicoServicio;
    }

    // LISTAR TODOS LOS HORARIOS
    @GetMapping
    public ResponseEntity<List<HorarioMedico>> listarTodos() {

        return ResponseEntity.ok(
                horarioMedicoServicio.listarTodosLosHorarios()
        );
    }

    // REGISTRAR HORARIO DE UN MEDICO
    @PostMapping("/{idMedico}")
    public ResponseEntity<?> registrarHorario(
            @PathVariable Long idMedico,
            @RequestBody HorarioMedico request) {

        try {

            HorarioMedico horario =
                    horarioMedicoServicio.registrarHorario(
                            idMedico,
                            request.getDiaSemana(),
                            request.getHoraInicio(),
                            request.getHoraFin()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(horario);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // LISTAR HORARIOS DE UN MEDICO
    @GetMapping("/medico/{idMedico}")
    public ResponseEntity<?> listarHorarios(
            @PathVariable Long idMedico) {

        try {

            List<HorarioMedico> horarios =
                    horarioMedicoServicio.listarHorarios(idMedico);

            return ResponseEntity.ok(horarios);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // EDITAR HORARIO
    @PutMapping("/{idHorario}")
    public ResponseEntity<?> editarHorario(
            @PathVariable Long idHorario,
            @RequestBody HorarioMedico request) {

        try {

            HorarioMedico horario =
                    horarioMedicoServicio.editarHorario(
                            idHorario,
                            request.getDiaSemana(),
                            request.getHoraInicio(),
                            request.getHoraFin()
                    );

            return ResponseEntity.ok(horario);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }

    // ACTIVAR O DESACTIVAR HORARIO
    @PatchMapping("/{idHorario}/estado")
    public ResponseEntity<?> cambiarEstadoHorario(
            @PathVariable Long idHorario,
            @RequestBody Map<String, Boolean> request) {

        try {

            Boolean activo = request.get("activo");

            HorarioMedico horario =
                    horarioMedicoServicio.cambiarEstadoHorario(
                            idHorario,
                            activo
                    );

            return ResponseEntity.ok(horario);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}

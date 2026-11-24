
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.Entidad.HorarioMedico;
import com.Meditrack.Meditrack.Entidad.Medico;
import com.Meditrack.Meditrack.Repositorio.HorarioMedicoRepositorio;
import com.Meditrack.Meditrack.Repositorio.MedicoRepositorio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class HorarioMedicoServicio {

    private final HorarioMedicoRepositorio horarioRepositorio;
    private final MedicoRepositorio medicoRepositorio;

    private static final Set<String> DIAS_VALIDOS = Set.of(
            "lunes", "martes", "miércoles",
            "jueves", "viernes", "sábado", "domingo"
    );

    public HorarioMedicoServicio(
            HorarioMedicoRepositorio horarioRepositorio,
            MedicoRepositorio medicoRepositorio) {

        this.horarioRepositorio = horarioRepositorio;
        this.medicoRepositorio = medicoRepositorio;
    }

    // REGISTRAR HORARIO DE UN MEDICO
    @Transactional
    public HorarioMedico registrarHorario(
            Long idMedico,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin) {

        Medico medico = buscarMedico(idMedico);

        validarMedicoActivo(medico);
        validarDatosHorario(diaSemana, horaInicio, horaFin);

        String dia = normalizarDia(diaSemana);

        validarCruceHorario(
                idMedico, dia, horaInicio, horaFin, null
        );

        HorarioMedico horario = new HorarioMedico();

        horario.setMedico(medico);
        horario.setDiaSemana(dia);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);
        horario.setActivo(true);

        return horarioRepositorio.save(horario);
    }

    // LISTAR TODOS LOS HORARIOS
    @Transactional(readOnly = true)
    public List<HorarioMedico> listarTodosLosHorarios() {

        return horarioRepositorio.findAll();
    }

    // LISTAR HORARIOS DE UN MEDICO
    @Transactional(readOnly = true)
    public List<HorarioMedico> listarHorarios(Long idMedico) {

        if (!medicoRepositorio.existsById(idMedico)) {
            throw new IllegalArgumentException(
                    "No se encontró el médico indicado."
            );
        }

        return horarioRepositorio.findByMedico_IdMedico(idMedico);
    }

    // EDITAR HORARIO
    @Transactional
    public HorarioMedico editarHorario(
            Long idHorario,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin) {

        HorarioMedico horario = buscarHorario(idHorario);

        validarDatosHorario(diaSemana, horaInicio, horaFin);

        Medico medico = horario.getMedico();

        validarMedicoActivo(medico);

        String dia = normalizarDia(diaSemana);

        if (Boolean.TRUE.equals(horario.getActivo())) {
            validarCruceHorario(
                    medico.getIdMedico(),
                    dia,
                    horaInicio,
                    horaFin,
                    idHorario
            );
        }

        horario.setDiaSemana(dia);
        horario.setHoraInicio(horaInicio);
        horario.setHoraFin(horaFin);

        return horarioRepositorio.save(horario);
    }

    // ACTIVAR O DESACTIVAR HORARIO
    @Transactional
    public HorarioMedico cambiarEstadoHorario(
            Long idHorario,
            Boolean activo) {

        if (activo == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el estado del horario."
            );
        }

        HorarioMedico horario = buscarHorario(idHorario);

        if (activo) {
            validarMedicoActivo(horario.getMedico());

            validarCruceHorario(
                    horario.getMedico().getIdMedico(),
                    normalizarDia(horario.getDiaSemana()),
                    horario.getHoraInicio(),
                    horario.getHoraFin(),
                    idHorario
            );
        }

        horario.setActivo(activo);

        return horarioRepositorio.save(horario);
    }

    // BUSCAR MEDICO
    private Medico buscarMedico(Long idMedico) {

        return medicoRepositorio.findById(idMedico)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el médico indicado."
                ));
    }

    // BUSCAR HORARIO
    private HorarioMedico buscarHorario(Long idHorario) {

        return horarioRepositorio.findById(idHorario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el horario indicado."
                ));
    }

    // VALIDAR ESTADO DEL MEDICO
    private void validarMedicoActivo(Medico medico) {

        if (medico.getUsuario() == null
                || medico.getUsuario().getEstado() == null
                || !medico.getUsuario().getEstado()
                        .equalsIgnoreCase("Activo")) {

            throw new IllegalArgumentException(
                    "No se pueden gestionar horarios de un médico inactivo."
            );
        }
    }

    // VALIDAR DATOS DEL HORARIO
    private void validarDatosHorario(
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin) {

        if (diaSemana == null || diaSemana.isBlank()) {
            throw new IllegalArgumentException(
                    "El día de la semana es obligatorio."
            );
        }

        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException(
                    "Las horas de inicio y fin son obligatorias."
            );
        }

        if (!horaInicio.isBefore(horaFin)) {
            throw new IllegalArgumentException(
                    "La hora de inicio debe ser anterior a la hora de fin."
            );
        }

        String dia = normalizarDia(diaSemana);

        if (!DIAS_VALIDOS.contains(dia)) {
            throw new IllegalArgumentException(
                    "El día indicado no es válido."
            );
        }
    }

    // NORMALIZAR DIA
    private String normalizarDia(String diaSemana) {

        return diaSemana.trim().toLowerCase();
    }

    // VALIDAR CRUCES DE HORARIOS
    private void validarCruceHorario(
            Long idMedico,
            String diaSemana,
            LocalTime horaInicio,
            LocalTime horaFin,
            Long idHorarioExcluir) {

        List<HorarioMedico> horarios =
                horarioRepositorio
                        .findByMedico_IdMedicoAndDiaSemanaIgnoreCase(
                                idMedico, diaSemana
                        );

        for (HorarioMedico existente : horarios) {

            if (!Boolean.TRUE.equals(existente.getActivo())) {
                continue;
            }

            if (idHorarioExcluir != null
                    && existente.getIdHorario()
                            .equals(idHorarioExcluir)) {
                continue;
            }

            boolean seSuperpone =
                    horaInicio.isBefore(existente.getHoraFin())
                    && horaFin.isAfter(existente.getHoraInicio());

            if (seSuperpone) {
                throw new IllegalArgumentException(
                        "El médico ya tiene un horario activo "
                        + "que se cruza con el horario indicado."
                );
            }
        }
    }
}

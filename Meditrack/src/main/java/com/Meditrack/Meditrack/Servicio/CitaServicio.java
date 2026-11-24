package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.Entidad.Cita;
import com.Meditrack.Meditrack.Entidad.Paciente;
import com.Meditrack.Meditrack.Entidad.Medico;
import com.Meditrack.Meditrack.Entidad.HorarioMedico;

import com.Meditrack.Meditrack.Repositorio.CitaRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteRepositorio;
import com.Meditrack.Meditrack.Repositorio.MedicoRepositorio;
import com.Meditrack.Meditrack.Repositorio.HorarioMedicoRepositorio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
public class CitaServicio {

    private final CitaRepositorio citaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final MedicoRepositorio medicoRepositorio;
    private final HorarioMedicoRepositorio horarioMedicoRepositorio;

    public CitaServicio(
            CitaRepositorio citaRepositorio,
            PacienteRepositorio pacienteRepositorio,
            MedicoRepositorio medicoRepositorio,
            HorarioMedicoRepositorio horarioMedicoRepositorio) {

        this.citaRepositorio = citaRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        this.medicoRepositorio = medicoRepositorio;
        this.horarioMedicoRepositorio = horarioMedicoRepositorio;
    }

    // REGISTRAR CITA MEDICA
    @Transactional
    public Cita registrarCita(
            Long idUsuarioPaciente,
            Long idMedico,
            LocalDateTime fechaHora,
            String motivo) {

        // VALIDAR FECHA
        if (fechaHora == null) {
            throw new IllegalArgumentException(
                    "Debes indicar la fecha y hora de la cita.");
        }

        if (!fechaHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La fecha de la cita debe ser futura.");
        }

        // VALIDAR MOTIVO
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar el motivo de la cita.");
        }

        // BUSCAR PERFIL DEL PACIENTE
        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuarioPaciente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del paciente."));

        // VALIDAR ESTADO DEL PACIENTE
        if (!"Activo".equalsIgnoreCase(
                paciente.getUsuario().getEstado())) {

            throw new IllegalArgumentException(
                    "Tu cuenta está inactiva. No puedes registrar citas.");
        }

        // BUSCAR MEDICO
        Medico medico = medicoRepositorio.findById(idMedico)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el médico indicado."));

        // VALIDAR ESTADO DEL MEDICO
        if (!"Activo".equalsIgnoreCase(
                medico.getUsuario().getEstado())) {

            throw new IllegalArgumentException(
                    "No se pueden registrar citas con un médico inactivo.");
        }

        // VALIDAR DIA DE LA SEMANA
        DayOfWeek dia = fechaHora.getDayOfWeek();

        String diaSemana = dia.getDisplayName(
                TextStyle.FULL,
                new Locale("es", "PE"));

        // BUSCAR HORARIOS DEL MEDICO PARA ESE DIA
        List<HorarioMedico> horarios =
                horarioMedicoRepositorio
                        .findByMedico_IdMedicoAndDiaSemanaIgnoreCase(
                                idMedico,
                                diaSemana);

        LocalTime horaCita = fechaHora.toLocalTime();

        // VALIDAR HORARIO ACTIVO
        boolean horarioDisponible = horarios.stream()
                .anyMatch(horario ->
                        Boolean.TRUE.equals(horario.getActivo())
                        && !horaCita.isBefore(horario.getHoraInicio())
                        && horaCita.isBefore(horario.getHoraFin())
                );

        if (!horarioDisponible) {
            throw new IllegalArgumentException(
                    "El médico no tiene horario disponible para esa fecha y hora.");
        }

        // VALIDAR QUE EL MEDICO NO TENGA OTRA CITA
        boolean medicoOcupado = citaRepositorio
                .findByMedico_IdMedico(idMedico)
                .stream()
                .anyMatch(cita ->
                        cita.getFechaHora().equals(fechaHora)
                        && !"Cancelada".equalsIgnoreCase(cita.getEstado())
                );

        if (medicoOcupado) {
            throw new IllegalArgumentException(
                    "El médico ya tiene una cita registrada en ese horario.");
        }

        // VALIDAR QUE EL PACIENTE NO TENGA OTRA CITA
        boolean pacienteOcupado = citaRepositorio
                .findByPaciente_IdPaciente(paciente.getIdPaciente())
                .stream()
                .anyMatch(cita ->
                        cita.getFechaHora().equals(fechaHora)
                        && !"Cancelada".equalsIgnoreCase(cita.getEstado())
                );

        if (pacienteOcupado) {
            throw new IllegalArgumentException(
                    "El paciente ya tiene una cita registrada en ese horario.");
        }

        // CREAR CITA
        Cita nuevaCita = new Cita();

        nuevaCita.setPaciente(paciente);
        nuevaCita.setMedico(medico);
        nuevaCita.setFechaHora(fechaHora);
        nuevaCita.setMotivo(motivo.trim());
        nuevaCita.setEstado("Pendiente");

        return citaRepositorio.save(nuevaCita);
    }

    // LISTAR CITAS DEL PACIENTE
    @Transactional(readOnly = true)
    public List<Cita> listarCitasPaciente(Long idUsuarioPaciente) {

        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuarioPaciente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del paciente."));

        return citaRepositorio
                .findByPaciente_IdPaciente(paciente.getIdPaciente());
    }

    // LISTAR CITAS DEL MEDICO AUTENTICADO
    @Transactional(readOnly = true)
    public List<Cita> listarCitasMedico(Long idUsuarioMedico) {

        Medico medico = medicoRepositorio
                .findByUsuario_IdUsuario(idUsuarioMedico)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del médico."));

        return citaRepositorio
                .findByMedico_IdMedico(medico.getIdMedico());
    }

    // CANCELAR CITA DEL PACIENTE AUTENTICADO
    @Transactional
    public Cita cancelarCitaPaciente(
            Long idUsuarioPaciente,
            Long idCita) {

        Cita cita = citaRepositorio
                .findByIdCitaAndPaciente_Usuario_IdUsuario(
                        idCita,
                        idUsuarioPaciente)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró la cita o no te pertenece."));

        // VALIDAR ESTADO
        if ("Cancelada".equalsIgnoreCase(cita.getEstado())) {
            throw new IllegalArgumentException(
                    "La cita ya se encuentra cancelada.");
        }

        // VALIDAR FECHA
        if (!cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "No puedes cancelar una cita que ya pasó.");
        }

        cita.setEstado("Cancelada");

        return citaRepositorio.save(cita);
    }

    // CANCELAR CITA DEL MEDICO AUTENTICADO
    @Transactional
    public Cita cancelarCitaMedico(
            Long idUsuarioMedico,
            Long idCita) {

        Cita cita = citaRepositorio
                .findByIdCitaAndMedico_Usuario_IdUsuario(
                        idCita,
                        idUsuarioMedico)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró la cita o no te pertenece."));

        // VALIDAR ESTADO
        if ("Cancelada".equalsIgnoreCase(cita.getEstado())) {
            throw new IllegalArgumentException(
                    "La cita ya se encuentra cancelada.");
        }

        // VALIDAR FECHA
        if (!cita.getFechaHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "No puedes cancelar una cita que ya pasó.");
        }

        cita.setEstado("Cancelada");

        return citaRepositorio.save(cita);
    }
}
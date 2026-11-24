package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CitaRepositorio extends JpaRepository<Cita, Long> {

    // Listar citas de un paciente
    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    // Listar citas de un médico
    List<Cita> findByMedico_IdMedico(Long idMedico);

    // Buscar una cita perteneciente a un paciente
    Optional<Cita> findByIdCitaAndPaciente_Usuario_IdUsuario(
            Long idCita,
            Long idUsuario
    );

    // Buscar una cita perteneciente a un médico
    Optional<Cita> findByIdCitaAndMedico_Usuario_IdUsuario(
            Long idCita,
            Long idUsuario
    );
}
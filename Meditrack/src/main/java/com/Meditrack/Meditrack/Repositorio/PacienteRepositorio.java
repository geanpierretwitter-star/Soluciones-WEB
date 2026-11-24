package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PacienteRepositorio
        extends JpaRepository<Paciente, Long> {

        Optional<Paciente> findByUsuario_IdUsuario(Long idUsuario);
}
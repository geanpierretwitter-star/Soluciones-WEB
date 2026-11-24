
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.HistoriaClinica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistoriaClinicaRepositorio
        extends JpaRepository<HistoriaClinica, Long> {

    Optional<HistoriaClinica> findByPaciente_IdPaciente(Long idPaciente);
}

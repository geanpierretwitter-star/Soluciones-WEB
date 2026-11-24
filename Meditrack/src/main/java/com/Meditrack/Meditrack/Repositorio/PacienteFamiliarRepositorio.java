
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PacienteFamiliarRepositorio
        extends JpaRepository<PacienteFamiliar, PacienteFamiliar.PacienteFamiliarId> {

    List<PacienteFamiliar> findByIdPaciente(Long idPaciente);

    List<PacienteFamiliar> findByIdFamilia(Long idFamilia);
}

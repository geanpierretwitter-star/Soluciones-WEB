
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.HorarioMedico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioMedicoRepositorio
        extends JpaRepository<HorarioMedico, Long> {

    List<HorarioMedico> findByMedico_IdMedico(Long idMedico);

    List<HorarioMedico> findByMedico_IdMedicoAndDiaSemanaIgnoreCase(
            Long idMedico, String diaSemana);
List<HorarioMedico> findByMedico_IdMedicoAndDiaSemanaIgnoreCaseAndActivoTrue(
        Long idMedico, String diaSemana);
        }

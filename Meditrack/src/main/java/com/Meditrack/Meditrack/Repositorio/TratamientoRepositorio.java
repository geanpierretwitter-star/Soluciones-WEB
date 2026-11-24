
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TratamientoRepositorio
        extends JpaRepository<Tratamiento, Long> {

    List<Tratamiento> findByPaciente_IdPaciente(Long idPaciente);

    List<Tratamiento> findByMedico_IdMedico(Long idMedico);
}

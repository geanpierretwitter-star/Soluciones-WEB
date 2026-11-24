
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.DetalleTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleTratamientoRepositorio
        extends JpaRepository<DetalleTratamiento, Long> {

    List<DetalleTratamiento> findByTratamiento_IdTratamiento(Long idTratamiento);

    List<DetalleTratamiento> findByMedicamento_IdMedicamento(Long idMedicamento);
}

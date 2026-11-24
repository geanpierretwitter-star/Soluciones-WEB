
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicamentoRepositorio
        extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByNombreContainingIgnoreCase(String nombre);

    List<Medicamento> findByEstadoIgnoreCase(String estado);
}

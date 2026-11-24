
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.CodigoVinculacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CodigoVinculacionRepositorio
        extends JpaRepository<CodigoVinculacion, Long> {

    Optional<CodigoVinculacion> findByCodigo(String codigo);
}

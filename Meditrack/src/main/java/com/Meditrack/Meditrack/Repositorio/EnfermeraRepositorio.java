
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Enfermera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnfermeraRepositorio
        extends JpaRepository<Enfermera, Long> {

    Optional<Enfermera> findByUsuario_IdUsuario(Long idUsuario);

    boolean existsByColegiatura(String colegiatura);
    boolean existsByColegiaturaAndIdEnfermeraNot(
    String colegiatura, Long idEnfermera
);
}

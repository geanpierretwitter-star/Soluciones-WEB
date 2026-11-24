
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamiliarRepositorio extends JpaRepository<Familiar, Long> {

    Optional<Familiar> findByUsuario_IdUsuario(Long idUsuario);
}

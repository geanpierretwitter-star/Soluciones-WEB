
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicoRepositorio extends JpaRepository<Medico, Long> {

    Optional<Medico> findByUsuario_IdUsuario(Long idUsuario);

    boolean existsByCmp(String cmp);
    boolean existsByCmpAndIdMedicoNot(String cmp, Long idMedico);
}

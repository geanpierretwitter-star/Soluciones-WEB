
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepositorio extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuario_IdUsuario(Long idUsuario);

    List<Auditoria> findByAccionIgnoreCase(String accion);
}

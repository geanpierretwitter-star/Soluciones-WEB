
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepositorio
        extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByUsuario_IdUsuario(Long idUsuario);

    List<Notificacion> findByUsuario_IdUsuarioAndLeidaFalse(Long idUsuario);
}

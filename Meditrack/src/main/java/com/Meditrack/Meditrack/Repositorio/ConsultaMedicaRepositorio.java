
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.ConsultaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsultaMedicaRepositorio
        extends JpaRepository<ConsultaMedica, Long> {

    Optional<ConsultaMedica> findByCita_IdCita(Long idCita);
}

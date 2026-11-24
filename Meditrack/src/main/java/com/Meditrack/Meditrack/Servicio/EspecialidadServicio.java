
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.Entidad.Especialidad;
import com.Meditrack.Meditrack.Repositorio.EspecialidadRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadServicio {

    private final EspecialidadRepositorio especialidadRepositorio;

    public EspecialidadServicio(
            EspecialidadRepositorio especialidadRepositorio) {
        this.especialidadRepositorio = especialidadRepositorio;
    }

    public List<Especialidad> listarActivas() {
        return especialidadRepositorio.findAll()
                .stream()
                .filter(especialidad ->
                        "Activo".equalsIgnoreCase(especialidad.getEstado()))
                .toList();
    }
}

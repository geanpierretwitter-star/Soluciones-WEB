
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.Entidad.Especialidad;
import com.Meditrack.Meditrack.Servicio.EspecialidadServicio;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/especialidades")
@CrossOrigin(origins = "http://localhost:4200")
public class EspecialidadController {

    private final EspecialidadServicio especialidadServicio;

    public EspecialidadController(
            EspecialidadServicio especialidadServicio) {
        this.especialidadServicio = especialidadServicio;
    }

    @GetMapping
    public ResponseEntity<List<Especialidad>> listarActivas() {

        List<Especialidad> especialidades =
                especialidadServicio.listarActivas();

        return ResponseEntity.ok(especialidades);
    }
}

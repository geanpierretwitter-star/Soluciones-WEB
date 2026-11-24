
package com.Meditrack.Meditrack.Controlador;

import com.Meditrack.Meditrack.Entidad.Medico;
import com.Meditrack.Meditrack.Servicio.MedicoServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicoController {

    private final MedicoServicio medicoServicio;

    public MedicoController(MedicoServicio medicoServicio) {
        this.medicoServicio = medicoServicio;
    }

    // Listar todos los médicos
    @GetMapping
    public ResponseEntity<List<Medico>> listarMedicos() {
        return ResponseEntity.ok(medicoServicio.listarMedicos());
    }

    // Buscar médico por ID
    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable Long id) {
        return medicoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

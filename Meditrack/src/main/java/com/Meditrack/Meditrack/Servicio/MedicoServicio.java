
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.Entidad.Medico;
import com.Meditrack.Meditrack.Repositorio.MedicoRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoServicio {

    private final MedicoRepositorio medicoRepositorio;

    public MedicoServicio(MedicoRepositorio medicoRepositorio) {
        this.medicoRepositorio = medicoRepositorio;
    }

    // Listar todos los médicos
    public List<Medico> listarMedicos() {
        return medicoRepositorio.findAll();
    }

    // Buscar médico por su ID
    public Optional<Medico> buscarPorId(Long id) {
        return medicoRepositorio.findById(id);
    }

    // Buscar médico asociado a un usuario
    public Optional<Medico> buscarPorUsuario(Long idUsuario) {
        return medicoRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    // Verificar si existe un CMP
    public boolean existeCmp(String cmp) {
        return medicoRepositorio.existsByCmp(cmp);
    }
}

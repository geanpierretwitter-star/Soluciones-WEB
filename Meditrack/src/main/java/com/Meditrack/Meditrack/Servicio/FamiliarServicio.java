
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.DTO.PacienteVinculadoDTO;

import com.Meditrack.Meditrack.Entidad.CodigoVinculacion;
import com.Meditrack.Meditrack.Entidad.Familiar;
import com.Meditrack.Meditrack.Entidad.Paciente;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;

import com.Meditrack.Meditrack.Repositorio.CodigoVinculacionRepositorio;
import com.Meditrack.Meditrack.Repositorio.FamiliarRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteFamiliarRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteRepositorio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FamiliarServicio {

    private final FamiliarRepositorio familiarRepositorio;
    private final CodigoVinculacionRepositorio codigoVinculacionRepositorio;
    private final PacienteFamiliarRepositorio pacienteFamiliarRepositorio;
    private final PacienteRepositorio pacienteRepositorio;

    public FamiliarServicio(
            FamiliarRepositorio familiarRepositorio,
            CodigoVinculacionRepositorio codigoVinculacionRepositorio,
            PacienteFamiliarRepositorio pacienteFamiliarRepositorio,
            PacienteRepositorio pacienteRepositorio) {

        this.familiarRepositorio = familiarRepositorio;
        this.codigoVinculacionRepositorio = codigoVinculacionRepositorio;
        this.pacienteFamiliarRepositorio = pacienteFamiliarRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
    }

    // BUSCAR FAMILIAR POR ID DE USUARIO
    public Optional<Familiar> buscarPorIdUsuario(Long idUsuario) {
        return familiarRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    // VINCULAR FAMILIAR CON PACIENTE MEDIANTE CÓDIGO
    @Transactional
    public PacienteFamiliar vincularConCodigo(
            Long idUsuario,
            String codigoIngresado,
            String parentesco) {

        if (codigoIngresado == null || codigoIngresado.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes ingresar el código de vinculación.");
        }

        if (parentesco == null || parentesco.isBlank()
                || parentesco.trim().length() > 50) {
            throw new IllegalArgumentException(
                    "Debes ingresar un parentesco válido de máximo 50 caracteres.");
        }

        Familiar familiar = familiarRepositorio
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el perfil del familiar."));

        CodigoVinculacion codigo = codigoVinculacionRepositorio
                .findByCodigo(codigoIngresado.trim())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El código no existe o no es válido."));

        if (Boolean.TRUE.equals(codigo.getUsado())) {
            throw new IllegalArgumentException(
                    "Este código ya fue utilizado.");
        }

        if (!LocalDateTime.now().isBefore(codigo.getFechaExpiracion())) {
            throw new IllegalArgumentException(
                    "El código ha vencido.");
        }

        Long idPaciente = codigo.getPaciente().getIdPaciente();
        Long idFamilia = familiar.getIdFamilia();

        PacienteFamiliar.PacienteFamiliarId idRelacion =
                new PacienteFamiliar.PacienteFamiliarId(
                        idPaciente, idFamilia);

        if (pacienteFamiliarRepositorio.existsById(idRelacion)) {
            throw new IllegalArgumentException(
                    "Este familiar ya está vinculado con el paciente.");
        }

        PacienteFamiliar relacion = new PacienteFamiliar();
        relacion.setIdPaciente(idPaciente);
        relacion.setIdFamilia(idFamilia);
        relacion.setParentesco(parentesco.trim());
        relacion.setAutorizadoCitas(false);

        PacienteFamiliar relacionGuardada =
                pacienteFamiliarRepositorio.save(relacion);

        codigo.setUsado(true);
        codigoVinculacionRepositorio.save(codigo);

        return relacionGuardada;
    }

    // LISTAR PACIENTES VINCULADOS CON SUS DATOS PERSONALES
    @Transactional(readOnly = true)
    public List<PacienteVinculadoDTO> listarPacientesVinculados(
            Long idUsuario) {

        Familiar familiar = familiarRepositorio
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el perfil del familiar."));

        Long idFamilia = familiar.getIdFamilia();

        List<PacienteFamiliar> relaciones =
                pacienteFamiliarRepositorio.findByIdFamilia(idFamilia);

        List<PacienteVinculadoDTO> resultado = new ArrayList<>();

        for (PacienteFamiliar relacion : relaciones) {

            Paciente paciente = pacienteRepositorio
                    .findById(relacion.getIdPaciente())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se encontró el paciente vinculado."));

            PacienteVinculadoDTO dto = new PacienteVinculadoDTO(
                    paciente.getIdPaciente(),
                    paciente.getUsuario().getNombre(),
                    paciente.getUsuario().getApellidos(),
                    paciente.getUsuario().getDni(),
                    relacion.getParentesco(),
                    relacion.getAutorizadoCitas()
            );

            resultado.add(dto);
        }

        return resultado;
    }
}

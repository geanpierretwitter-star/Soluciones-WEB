
package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.Entidad.CodigoVinculacion;
import com.Meditrack.Meditrack.Entidad.Paciente;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;

import com.Meditrack.Meditrack.Repositorio.CodigoVinculacionRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteFamiliarRepositorio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteServicio {

    private final PacienteRepositorio pacienteRepositorio;
    private final CodigoVinculacionRepositorio codigoVinculacionRepositorio;
    private final PacienteFamiliarRepositorio pacienteFamiliarRepositorio;

    private final SecureRandom generadorAleatorio = new SecureRandom();

    public PacienteServicio(
            PacienteRepositorio pacienteRepositorio,
            CodigoVinculacionRepositorio codigoVinculacionRepositorio,
            PacienteFamiliarRepositorio pacienteFamiliarRepositorio) {

        this.pacienteRepositorio = pacienteRepositorio;
        this.codigoVinculacionRepositorio = codigoVinculacionRepositorio;
        this.pacienteFamiliarRepositorio = pacienteFamiliarRepositorio;
    }

    // BUSCAR PACIENTE POR ID DE USUARIO
    public Optional<Paciente> buscarPorIdUsuario(Long idUsuario) {

        return pacienteRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    // LISTAR FAMILIARES VINCULADOS AL PACIENTE
    @Transactional(readOnly = true)
    public List<PacienteFamiliar> listarFamiliaresVinculados(
            Long idUsuario) {

        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del paciente."));

        return pacienteFamiliarRepositorio
                .findByIdPaciente(paciente.getIdPaciente());
    }

    // CAMBIAR AUTORIZACION DE CITAS DEL FAMILIAR
    @Transactional
    public PacienteFamiliar cambiarAutorizacionFamiliar(
            Long idUsuario,
            Long idFamilia,
            Boolean autorizado) {

        // VALIDAR QUE SE HAYA ENVIADO LA AUTORIZACION
        if (autorizado == null) {
            throw new IllegalArgumentException(
                    "Debes indicar el estado de la autorización.");
        }

        // BUSCAR EL PERFIL DEL PACIENTE AUTENTICADO
        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del paciente."));

        // CONSTRUIR LA CLAVE PRIMARIA COMPUESTA
        PacienteFamiliar.PacienteFamiliarId idRelacion =
                new PacienteFamiliar.PacienteFamiliarId(
                        paciente.getIdPaciente(),
                        idFamilia
                );

        // BUSCAR LA RELACION ENTRE PACIENTE Y FAMILIAR
        PacienteFamiliar relacion = pacienteFamiliarRepositorio
                .findById(idRelacion)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El familiar no está vinculado con este paciente."));

        // ACTUALIZAR LA AUTORIZACION
        relacion.setAutorizadoCitas(autorizado);

        // GUARDAR LOS CAMBIOS
        return pacienteFamiliarRepositorio.save(relacion);
    }

    // GENERAR CODIGO DE VINCULACION
    @Transactional
    public CodigoVinculacion generarCodigoVinculacion(Long idUsuario) {

        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuario)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No se encontró el perfil del paciente."));

        String codigo;

        do {
            codigo = generarCodigoAleatorio();
        } while (codigoVinculacionRepositorio
                .findByCodigo(codigo).isPresent());

        CodigoVinculacion nuevoCodigo = new CodigoVinculacion();

        nuevoCodigo.setPaciente(paciente);
        nuevoCodigo.setCodigo(codigo);
        nuevoCodigo.setFechaExpiracion(
                LocalDateTime.now().plusMinutes(15));
        nuevoCodigo.setUsado(false);

        return codigoVinculacionRepositorio.save(nuevoCodigo);
    }

    // GENERAR CODIGO ALEATORIO
    private String generarCodigoAleatorio() {

        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

        StringBuilder codigo = new StringBuilder("MT-");

        for (int i = 0; i < 8; i++) {

            codigo.append(
                    caracteres.charAt(
                            generadorAleatorio.nextInt(
                                    caracteres.length())));
        }

        return codigo.toString();
    }
}

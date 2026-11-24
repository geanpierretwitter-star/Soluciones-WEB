package com.Meditrack.Meditrack.Servicio;

import com.Meditrack.Meditrack.DTO.RegistroPacienteRequest;
import com.Meditrack.Meditrack.DTO.RegistroMedicoRequest;
import com.Meditrack.Meditrack.DTO.RegistroEnfermeraRequest;
import com.Meditrack.Meditrack.DTO.UsuarioRespuestaDTO;
import com.Meditrack.Meditrack.DTO.PacienteVinculadoDTO;

import com.Meditrack.Meditrack.Entidad.Paciente;
import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Entidad.Medico;
import com.Meditrack.Meditrack.Entidad.Enfermera;
import com.Meditrack.Meditrack.Entidad.Especialidad;
import com.Meditrack.Meditrack.Entidad.Familiar;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;

import com.Meditrack.Meditrack.Repositorio.FamiliarRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteRepositorio;
import com.Meditrack.Meditrack.Repositorio.UsuarioRepositorio;
import com.Meditrack.Meditrack.Repositorio.MedicoRepositorio;
import com.Meditrack.Meditrack.Repositorio.EnfermeraRepositorio;
import com.Meditrack.Meditrack.Repositorio.EspecialidadRepositorio;
import com.Meditrack.Meditrack.Repositorio.PacienteFamiliarRepositorio;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.Meditrack.Meditrack.DTO.PersonalClinicoDetalleDTO;
import com.Meditrack.Meditrack.DTO.ActualizarPersonalClinicoRequest;
import com.Meditrack.Meditrack.DTO.UsuarioAdminDetalleDTO;
import com.Meditrack.Meditrack.DTO.PacienteVinculadoDTO;
import com.Meditrack.Meditrack.Entidad.PacienteFamiliar;
import com.Meditrack.Meditrack.Repositorio.PacienteFamiliarRepositorio;

import java.util.List;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final MedicoRepositorio medicoRepositorio;
    private final EnfermeraRepositorio enfermeraRepositorio;
    private final EspecialidadRepositorio especialidadRepositorio;
    private final FamiliarRepositorio familiarRepositorio;
    private final PacienteFamiliarRepositorio pacienteFamiliarRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServicio(
            UsuarioRepositorio usuarioRepositorio,
            PacienteRepositorio pacienteRepositorio,
            MedicoRepositorio medicoRepositorio,
            EnfermeraRepositorio enfermeraRepositorio,
            EspecialidadRepositorio especialidadRepositorio,
            FamiliarRepositorio familiarRepositorio,
            PacienteFamiliarRepositorio pacienteFamiliarRepositorio,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepositorio = usuarioRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        this.medicoRepositorio = medicoRepositorio;
        this.enfermeraRepositorio = enfermeraRepositorio;
        this.especialidadRepositorio = especialidadRepositorio;
        this.familiarRepositorio = familiarRepositorio;
        this.pacienteFamiliarRepositorio = pacienteFamiliarRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepositorio.existsByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public boolean existeDni(String dni) {
        return usuarioRepositorio.existsByDni(dni);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    // REGISTRO PÚBLICO DE PACIENTES
    @Transactional
    public UsuarioRespuestaDTO registrarPaciente(
            RegistroPacienteRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos del registro son obligatorios.");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (request.getApellidos() == null
                || request.getApellidos().isBlank()) {
            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios.");
        }

        // VALIDAR DNI
        if (request.getDni() == null
                || !request.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 dígitos.");
        }

        if (request.getCorreo() == null
                || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        if (request.getTelefono() == null
                || request.getTelefono().isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio.");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        String dni = request.getDni().trim();
        String correo = request.getCorreo().trim().toLowerCase();

        if (usuarioRepositorio.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese DNI.");
        }

        if (usuarioRepositorio.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(request.getTelefono().trim());

        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        usuario.setRol("PACIENTE");
        usuario.setEstado("Activo");

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuarioGuardado);

        pacienteRepositorio.save(paciente);

        return convertirRespuesta(usuarioGuardado);
    }

    // REGISTRO PÚBLICO DE FAMILIARES
    @Transactional
    public UsuarioRespuestaDTO registrarFamiliar(
            RegistroPacienteRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos del familiar son obligatorios.");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()
                || request.getApellidos() == null
                || request.getApellidos().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre y los apellidos son obligatorios.");
        }

        // VALIDAR DNI
        if (request.getDni() == null
                || !request.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 dígitos.");
        }

        if (request.getCorreo() == null
                || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        if (request.getTelefono() == null
                || request.getTelefono().isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio.");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        String dni = request.getDni().trim();
        String correo = request.getCorreo().trim().toLowerCase();

        if (usuarioRepositorio.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese DNI.");
        }

        if (usuarioRepositorio.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(request.getTelefono().trim());

        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        usuario.setRol("FAMILIAR");
        usuario.setEstado("Activo");

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        Familiar familiar = new Familiar();
        familiar.setUsuario(usuarioGuardado);

        familiarRepositorio.save(familiar);

        return convertirRespuesta(usuarioGuardado);
    }

    // REGISTRO DE MÉDICOS: SOLO ADMINISTRADOR
    @Transactional
    public UsuarioRespuestaDTO registrarMedico(
            RegistroMedicoRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos del médico son obligatorios.");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()
                || request.getApellidos() == null
                || request.getApellidos().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre y los apellidos son obligatorios.");
        }

        // VALIDAR DNI
        if (request.getDni() == null
                || !request.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 dígitos.");
        }

        if (request.getCorreo() == null
                || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        if (request.getTelefono() == null
                || request.getTelefono().isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio.");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (request.getCmp() == null
                || request.getCmp().isBlank()) {
            throw new IllegalArgumentException(
                    "El CMP es obligatorio.");
        }

        if (request.getIdEspecialidad() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una especialidad.");
        }

        String dni = request.getDni().trim();
        String correo = request.getCorreo().trim().toLowerCase();
        String cmp = request.getCmp().trim();

        if (usuarioRepositorio.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese DNI.");
        }

        if (usuarioRepositorio.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo.");
        }

        if (medicoRepositorio.existsByCmp(cmp)) {
            throw new IllegalArgumentException(
                    "Ya existe un médico con ese CMP.");
        }

        Especialidad especialidad = especialidadRepositorio
                .findById(request.getIdEspecialidad())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La especialidad seleccionada no existe."));

        if (!"Activo".equalsIgnoreCase(especialidad.getEstado())) {
            throw new IllegalArgumentException(
                    "La especialidad seleccionada no está activa.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(request.getTelefono().trim());

        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        usuario.setRol("MEDICO");
        usuario.setEstado("Activo");

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        Medico medico = new Medico();

        medico.setUsuario(usuarioGuardado);
        medico.setEspecialidad(especialidad);
        medico.setCmp(cmp);

        medicoRepositorio.save(medico);

        return convertirRespuesta(usuarioGuardado);
    }

    // REGISTRO DE ENFERMERAS: SOLO ADMINISTRADOR
    @Transactional
    public UsuarioRespuestaDTO registrarEnfermera(
            RegistroEnfermeraRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Los datos de la enfermera son obligatorios.");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()
                || request.getApellidos() == null
                || request.getApellidos().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre y los apellidos son obligatorios.");
        }

        // VALIDAR DNI
        if (request.getDni() == null
                || !request.getDni().matches("\\d{8}")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener exactamente 8 dígitos.");
        }

        if (request.getCorreo() == null
                || request.getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El correo es obligatorio.");
        }

        if (request.getTelefono() == null
                || request.getTelefono().isBlank()) {
            throw new IllegalArgumentException(
                    "El teléfono es obligatorio.");
        }

        if (request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (request.getColegiatura() == null
                || request.getColegiatura().isBlank()) {
            throw new IllegalArgumentException(
                    "La colegiatura es obligatoria.");
        }

        String dni = request.getDni().trim();
        String correo = request.getCorreo().trim().toLowerCase();
        String colegiatura = request.getColegiatura().trim();

        if (usuarioRepositorio.existsByDni(dni)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese DNI.");
        }

        if (usuarioRepositorio.existsByCorreo(correo)) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo.");
        }

        if (enfermeraRepositorio.existsByColegiatura(colegiatura)) {
            throw new IllegalArgumentException(
                    "Ya existe una enfermera con esa colegiatura.");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(request.getNombre().trim());
        usuario.setApellidos(request.getApellidos().trim());
        usuario.setDni(dni);
        usuario.setCorreo(correo);
        usuario.setTelefono(request.getTelefono().trim());

        usuario.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        usuario.setRol("ENFERMERA");
        usuario.setEstado("Activo");

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        Enfermera enfermera = new Enfermera();

        enfermera.setUsuario(usuarioGuardado);
        enfermera.setColegiatura(colegiatura);

        enfermeraRepositorio.save(enfermera);

        return convertirRespuesta(usuarioGuardado);
    }

    // LISTAR MÉDICOS Y ENFERMERAS
    @Transactional(readOnly = true)
    public List<UsuarioRespuestaDTO> listarPersonalClinico() {

        List<String> roles = List.of("MEDICO", "ENFERMERA");

        return usuarioRepositorio.findByRolIn(roles)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    // ACTIVAR O DESACTIVAR MÉDICOS Y ENFERMERAS
    @Transactional
    public UsuarioRespuestaDTO cambiarEstadoPersonalClinico(
            Long id, String estado) {

        if (estado == null
                || (!estado.equalsIgnoreCase("Activo")
                && !estado.equalsIgnoreCase("Inactivo"))) {
            throw new IllegalArgumentException(
                    "El estado debe ser Activo o Inactivo.");
        }

        Usuario usuario = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe."));

        if (!"MEDICO".equalsIgnoreCase(usuario.getRol())
                && !"ENFERMERA".equalsIgnoreCase(usuario.getRol())) {
            throw new IllegalArgumentException(
                    "Solo se puede cambiar el estado de médicos y enfermeras.");
        }

        String nuevoEstado = estado.equalsIgnoreCase("Activo")
                ? "Activo" : "Inactivo";

        usuario.setEstado(nuevoEstado);

        Usuario usuarioActualizado = usuarioRepositorio.save(usuario);

        return convertirRespuesta(usuarioActualizado);
    }
// LISTAR PACIENTES Y FAMILIARES
@Transactional(readOnly = true)
public List<UsuarioRespuestaDTO> listarPacientesYFamiliares() {

    List<String> roles = List.of("PACIENTE", "FAMILIAR");

    return usuarioRepositorio.findByRolIn(roles)
            .stream()
            .map(this::convertirRespuesta)
            .toList();
}
// ============================================
// CONSULTAR PACIENTES VINCULADOS A UN FAMILIAR
// ============================================

@Transactional(readOnly = true)
public List<PacienteVinculadoDTO> listarPacientesVinculadosFamiliar(
        Long idUsuarioFamiliar) {

    Usuario usuario = usuarioRepositorio.findById(idUsuarioFamiliar)
            .orElseThrow(() -> new IllegalArgumentException(
                    "El usuario no existe."));

    if (!"FAMILIAR".equalsIgnoreCase(usuario.getRol())) {
        throw new IllegalArgumentException(
                "El usuario seleccionado no es un familiar.");
    }

    Familiar familiar = familiarRepositorio
            .findByUsuario_IdUsuario(idUsuarioFamiliar)
            .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró el perfil del familiar."));

    List<PacienteFamiliar> vinculos =
            pacienteFamiliarRepositorio.findByIdFamilia(
                    familiar.getIdFamilia());

    return vinculos.stream().map(vinculo -> {

        Paciente paciente = pacienteRepositorio
                .findById(vinculo.getIdPaciente())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el paciente vinculado."));

        Usuario usuarioPaciente = paciente.getUsuario();

        return new PacienteVinculadoDTO(
                paciente.getIdPaciente(),
                usuarioPaciente.getNombre(),
                usuarioPaciente.getApellidos(),
                usuarioPaciente.getDni(),
                vinculo.getParentesco(),
                vinculo.getAutorizadoCitas()
        );

    }).toList();
}
// ============================================
// CONSULTAR DETALLES DE PACIENTES Y FAMILIARES
// ============================================

@Transactional(readOnly = true)
public UsuarioAdminDetalleDTO buscarDetalleUsuarioAdmin(Long id) {

    Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                    "El usuario no existe."));

    String rol = usuario.getRol();

    if (!"PACIENTE".equalsIgnoreCase(rol)
            && !"FAMILIAR".equalsIgnoreCase(rol)) {

        throw new IllegalArgumentException(
                "El usuario no es paciente ni familiar.");
    }

    UsuarioAdminDetalleDTO dto = new UsuarioAdminDetalleDTO();

    dto.setIdUsuario(usuario.getIdUsuario());
    dto.setNombre(usuario.getNombre());
    dto.setApellidos(usuario.getApellidos());
    dto.setDni(usuario.getDni());
    dto.setCorreo(usuario.getCorreo());
    dto.setTelefono(usuario.getTelefono());
    dto.setRol(usuario.getRol());
    dto.setEstado(usuario.getEstado());

    if ("PACIENTE".equalsIgnoreCase(rol)) {

        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos del paciente."));

        dto.setIdPaciente(paciente.getIdPaciente());

        if (paciente.getFechaNacimiento() != null) {
            dto.setFechaNacimiento(
                    paciente.getFechaNacimiento().toString());
        }

        dto.setGenero(paciente.getGenero());
        dto.setDireccion(paciente.getDireccion());

    } else {

        Familiar familiar = familiarRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos del familiar."));

        dto.setIdFamilia(familiar.getIdFamilia());

        if (familiar.getFechaRegistro() != null) {
            dto.setFechaRegistro(
                    familiar.getFechaRegistro().toString());
        }
    }

    return dto;
}

// ============================================
// ACTIVAR O DESACTIVAR PACIENTES Y FAMILIARES
// ============================================

@Transactional
public UsuarioRespuestaDTO cambiarEstadoUsuarioAdmin(
        Long id, String estado) {

    if (estado == null
            || (!estado.equalsIgnoreCase("Activo")
            && !estado.equalsIgnoreCase("Inactivo"))) {

        throw new IllegalArgumentException(
                "El estado debe ser Activo o Inactivo.");
    }

    Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                    "El usuario no existe."));

    String rol = usuario.getRol();

    if (!"PACIENTE".equalsIgnoreCase(rol)
            && !"FAMILIAR".equalsIgnoreCase(rol)) {

        throw new IllegalArgumentException(
                "Solo se puede cambiar el estado de pacientes y familiares.");
    }

    String nuevoEstado = estado.equalsIgnoreCase("Activo")
            ? "Activo" : "Inactivo";

    usuario.setEstado(nuevoEstado);

    Usuario usuarioActualizado = usuarioRepositorio.save(usuario);

    return convertirRespuesta(usuarioActualizado);
}

    
    // ============================================
    // CONSULTAR FAMILIARES VINCULADOS A UN PACIENTE
    // ============================================

    @Transactional(readOnly = true)
    public List<PacienteVinculadoDTO> listarFamiliaresVinculadosPaciente(
            Long idUsuarioPaciente) {

        Usuario usuario = usuarioRepositorio.findById(idUsuarioPaciente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El usuario no existe."));

        if (!"PACIENTE".equalsIgnoreCase(usuario.getRol())) {
            throw new IllegalArgumentException(
                    "El usuario seleccionado no es un paciente.");
        }

        Paciente paciente = pacienteRepositorio
                .findByUsuario_IdUsuario(idUsuarioPaciente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el perfil del paciente."));

        List<PacienteFamiliar> vinculos =
                pacienteFamiliarRepositorio.findByIdPaciente(
                        paciente.getIdPaciente());

        return vinculos.stream().map(vinculo -> {

            Familiar familiar = familiarRepositorio
                    .findById(vinculo.getIdFamilia())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "No se encontró uno de los familiares vinculados."));

            Usuario usuarioFamiliar = familiar.getUsuario();

            return new PacienteVinculadoDTO(
                    paciente.getIdPaciente(),
                    usuarioFamiliar.getNombre(),
                    usuarioFamiliar.getApellidos(),
                    usuarioFamiliar.getDni(),
                    vinculo.getParentesco(),
                    vinculo.getAutorizadoCitas()
            );

        }).toList();
    }

// ============================================
// CONSULTAR DETALLES DEL PERSONAL CLÍNICO
// ============================================

@Transactional(readOnly = true)
public PersonalClinicoDetalleDTO buscarDetallePersonalClinico(Long id) {

    Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                    "El usuario no existe."));

    if (!"MEDICO".equalsIgnoreCase(usuario.getRol())
            && !"ENFERMERA".equalsIgnoreCase(usuario.getRol())) {
        throw new IllegalArgumentException(
                "El usuario no pertenece al personal clínico.");
    }

    PersonalClinicoDetalleDTO dto = new PersonalClinicoDetalleDTO();

    dto.setIdUsuario(usuario.getIdUsuario());
    dto.setNombre(usuario.getNombre());
    dto.setApellidos(usuario.getApellidos());
    dto.setDni(usuario.getDni());
    dto.setCorreo(usuario.getCorreo());
    dto.setTelefono(usuario.getTelefono());
    dto.setRol(usuario.getRol());
    dto.setEstado(usuario.getEstado());

    if ("MEDICO".equalsIgnoreCase(usuario.getRol())) {

        Medico medico = medicoRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos del médico."));

        dto.setIdMedico(medico.getIdMedico());
        dto.setCmp(medico.getCmp());
        dto.setIdEspecialidad(
                medico.getEspecialidad().getIdEspecialidad());
        dto.setNombreEspecialidad(
                medico.getEspecialidad().getNombre());

    } else {

        Enfermera enfermera = enfermeraRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos de la enfermera."));

        dto.setIdEnfermera(enfermera.getIdEnfermera());
        dto.setColegiatura(enfermera.getColegiatura());
    }

    return dto;
}


// ============================================
// ACTUALIZAR PERSONAL CLÍNICO
// ============================================

@Transactional
public UsuarioRespuestaDTO actualizarPersonalClinico(
        Long id, ActualizarPersonalClinicoRequest request) {

    if (request == null) {
        throw new IllegalArgumentException(
                "Los datos de actualización son obligatorios.");
    }

    Usuario usuario = usuarioRepositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                    "El usuario no existe."));

    String rol = usuario.getRol();

    if (!"MEDICO".equalsIgnoreCase(rol)
            && !"ENFERMERA".equalsIgnoreCase(rol)) {
        throw new IllegalArgumentException(
                "Solo se puede actualizar médicos y enfermeras.");
    }

    // Validar datos personales
    if (request.getNombre() == null
            || request.getNombre().isBlank()
            || request.getApellidos() == null
            || request.getApellidos().isBlank()
            || request.getDni() == null
            || !request.getDni().trim().matches("[0-9]{8}")
            || request.getCorreo() == null
            || request.getCorreo().isBlank()
            || request.getTelefono() == null
            || request.getTelefono().isBlank()) {

        throw new IllegalArgumentException(
                "Complete correctamente todos los datos personales.");
    }

    String dni = request.getDni().trim();
    String correo = request.getCorreo().trim().toLowerCase();

    // Validar DNI y correo sin rechazar los propios del usuario
    if (usuarioRepositorio.existsByDniAndIdUsuarioNot(
            dni, id)) {
        throw new IllegalArgumentException(
                "El DNI ya pertenece a otro usuario.");
    }

    if (usuarioRepositorio.existsByCorreoAndIdUsuarioNot(
            correo, id)) {
        throw new IllegalArgumentException(
                "El correo ya pertenece a otro usuario.");
    }

    // Actualizar datos generales
    usuario.setNombre(request.getNombre().trim());
    usuario.setApellidos(request.getApellidos().trim());
    usuario.setDni(dni);
    usuario.setCorreo(correo);
    usuario.setTelefono(request.getTelefono().trim());

    if ("MEDICO".equalsIgnoreCase(rol)) {

        Medico medico = medicoRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos del médico."));

        if (request.getCmp() == null
                || request.getCmp().isBlank()
                || request.getIdEspecialidad() == null) {
            throw new IllegalArgumentException(
                    "El CMP y la especialidad son obligatorios.");
        }

        String cmp = request.getCmp().trim();

        if (medicoRepositorio.existsByCmpAndIdMedicoNot(
                cmp, medico.getIdMedico())) {
            throw new IllegalArgumentException(
                    "El CMP ya pertenece a otro médico.");
        }

        Especialidad especialidad = especialidadRepositorio
                .findById(request.getIdEspecialidad())
                .orElseThrow(() -> new IllegalArgumentException(
                        "La especialidad seleccionada no existe."));

        if (!"Activo".equalsIgnoreCase(especialidad.getEstado())) {
            throw new IllegalArgumentException(
                    "La especialidad seleccionada no está activa.");
        }

        medico.setCmp(cmp);
        medico.setEspecialidad(especialidad);

        medicoRepositorio.save(medico);

    } else {

        Enfermera enfermera = enfermeraRepositorio
                .findByUsuario_IdUsuario(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontraron los datos de la enfermera."));

        if (request.getColegiatura() == null
                || request.getColegiatura().isBlank()) {
            throw new IllegalArgumentException(
                    "La colegiatura es obligatoria.");
        }

        String colegiatura = request.getColegiatura().trim();

        if (enfermeraRepositorio.existsByColegiaturaAndIdEnfermeraNot(
                colegiatura, enfermera.getIdEnfermera())) {
            throw new IllegalArgumentException(
                    "La colegiatura ya pertenece a otra enfermera.");
        }

        enfermera.setColegiatura(colegiatura);

        enfermeraRepositorio.save(enfermera);
    }

    Usuario usuarioActualizado = usuarioRepositorio.save(usuario);

    return convertirRespuesta(usuarioActualizado);
}

    // CONVERTIR USUARIO A DTO DE RESPUESTA
    private UsuarioRespuestaDTO convertirRespuesta(Usuario usuario) {

        return new UsuarioRespuestaDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }
}
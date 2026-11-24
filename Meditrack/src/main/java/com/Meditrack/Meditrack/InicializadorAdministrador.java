
package com.Meditrack.Meditrack;

import com.Meditrack.Meditrack.Entidad.Usuario;
import com.Meditrack.Meditrack.Repositorio.UsuarioRepositorio;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InicializadorAdministrador implements ApplicationRunner {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public InicializadorAdministrador(
            UsuarioRepositorio usuarioRepositorio,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepositorio.existsByRol("ADMIN")) {
            System.out.println("Ya existe un administrador. No se creó otro.");
            return;
        }

        String nombre = environment.getProperty("MEDTRACK_ADMIN_NOMBRE");
        String apellidos = environment.getProperty("MEDTRACK_ADMIN_APELLIDOS");
        String dni = environment.getProperty("MEDTRACK_ADMIN_DNI");
        String correo = environment.getProperty("MEDTRACK_ADMIN_CORREO");
        String telefono = environment.getProperty("MEDTRACK_ADMIN_TELEFONO");
        String password = environment.getProperty("MEDTRACK_ADMIN_PASSWORD");

        if (nombre == null || apellidos == null || dni == null
                || correo == null || telefono == null || password == null
                || password.isBlank()) {
            System.out.println(
                "No se creó el administrador: faltan variables de entorno.");
            return;
        }

        if (usuarioRepositorio.existsByCorreo(correo)
                || usuarioRepositorio.existsByDni(dni)) {
            System.out.println(
                "No se creó el administrador: el correo o DNI ya existe.");
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre);
        admin.setApellidos(apellidos);
        admin.setDni(dni);
        admin.setCorreo(correo);
        admin.setTelefono(telefono);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRol("ADMIN");
        admin.setEstado("Activo");

        usuarioRepositorio.save(admin);

        System.out.println("Administrador inicial creado correctamente.");
    }
}

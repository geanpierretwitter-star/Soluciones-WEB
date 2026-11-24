
package com.Meditrack.Meditrack.Repositorio;

import com.Meditrack.Meditrack.Entidad.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio
        extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByDni(String dni);

    boolean existsByCorreo(String correo);

    boolean existsByRol(String rol);

    // Buscar médicos y enfermeras por sus roles
    List<Usuario> findByRolIn(List<String> roles);
    boolean existsByDniAndIdUsuarioNot(String dni, Long idUsuario);

boolean existsByCorreoAndIdUsuarioNot(String correo, Long idUsuario);
}

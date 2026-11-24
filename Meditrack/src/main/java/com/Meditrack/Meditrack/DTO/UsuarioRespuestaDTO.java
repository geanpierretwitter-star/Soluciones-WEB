
package com.Meditrack.Meditrack.DTO;

public class UsuarioRespuestaDTO {

    private Long idUsuario;
    private String nombre;
    private String apellidos;
    private String correo;
    private String rol;
    private String estado;

    public UsuarioRespuestaDTO() {
    }

    public UsuarioRespuestaDTO(
            Long idUsuario,
            String nombre,
            String apellidos,
            String correo,
            String rol,
            String estado) {

        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }
}

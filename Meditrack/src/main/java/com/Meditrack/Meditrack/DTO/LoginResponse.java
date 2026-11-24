
package com.Meditrack.Meditrack.DTO;

public class LoginResponse {

    private String token;
    private String tipo;
    private Long idUsuario;
    private String nombre;
    private String rol;

    public LoginResponse() {
    }

    public LoginResponse(
            String token,
            String tipo,
            Long idUsuario,
            String nombre,
            String rol) {

        this.token = token;
        this.tipo = tipo;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }
}

package com.Meditrack.Meditrack.DTO;

public class ActualizarPersonalClinicoRequest {

    private String nombre;
    private String apellidos;
    private String dni;
    private String correo;
    private String telefono;

    // Datos del médico
    private String cmp;
    private Long idEspecialidad;

    // Datos de la enfermera
    private String colegiatura;

    public ActualizarPersonalClinicoRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }

    public Long getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getColegiatura() {
        return colegiatura;
    }

    public void setColegiatura(String colegiatura) {
        this.colegiatura = colegiatura;
    }
}
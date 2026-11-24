
package com.Meditrack.Meditrack.DTO;

public class PacienteVinculadoDTO {

    private Long idPaciente;
    private String nombre;
    private String apellidos;
    private String dni;
    private String parentesco;
    private Boolean autorizadoCitas;


    public PacienteVinculadoDTO() {
    }

    public PacienteVinculadoDTO(
            Long idPaciente,
            String nombre,
            String apellidos,
            String dni,
            String parentesco,
            Boolean autorizadoCitas) {

        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.parentesco = parentesco;
        this.autorizadoCitas = autorizadoCitas;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
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

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public Boolean getAutorizadoCitas() {
        return autorizadoCitas;
    }

    public void setAutorizadoCitas(Boolean autorizadoCitas) {
        this.autorizadoCitas = autorizadoCitas;
    }
}

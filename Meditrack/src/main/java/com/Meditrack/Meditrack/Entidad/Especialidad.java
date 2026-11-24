
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "especialidad")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_especialidad")
    private Long idEspecialidad;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "Activo";

    public Especialidad() {
    }

    public Long getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(Long idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}

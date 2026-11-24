
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "codigo_vinculacion")
public class CodigoVinculacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_codigo")
    private Long idCodigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @Column(name = "codigo", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Column(name = "usado", nullable = false)
    private Boolean usado = false;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public CodigoVinculacion() {
    }

    public Long getIdCodigo() {
        return idCodigo;
    }

    public void setIdCodigo(Long idCodigo) {
        this.idCodigo = idCodigo;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getUsado() {
        return usado;
    }

    public void setUsado(Boolean usado) {
        this.usado = usado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}


package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha_accion", nullable = false,
            insertable = false, updatable = false)
    private LocalDateTime fechaAccion;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    public Auditoria() {
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaAccion() {
        return fechaAccion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }
}


package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "familiar")
public class Familiar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_familia")
    private Long idFamilia;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public Familiar() {
    }

    public Long getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Long idFamilia) {
        this.idFamilia = idFamilia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

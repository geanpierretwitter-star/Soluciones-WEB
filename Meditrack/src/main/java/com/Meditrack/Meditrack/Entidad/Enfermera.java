
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "enfermera")
public class Enfermera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_enfermera")
    private Long idEnfermera;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "colegiatura", nullable = false, unique = true, length = 20)
    private String colegiatura;

    public Enfermera() {
    }

    public Long getIdEnfermera() {
        return idEnfermera;
    }

    public void setIdEnfermera(Long idEnfermera) {
        this.idEnfermera = idEnfermera;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getColegiatura() {
        return colegiatura;
    }

    public void setColegiatura(String colegiatura) {
        this.colegiatura = colegiatura;
    }
}

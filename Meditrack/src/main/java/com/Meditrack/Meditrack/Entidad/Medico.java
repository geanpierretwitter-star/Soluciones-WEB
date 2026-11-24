
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "medico")
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Long idMedico;

    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_especialidad", nullable = false)
    private Especialidad especialidad;

    @Column(name = "cmp", nullable = false, unique = true, length = 20)
    private String cmp;

    public Medico() {
    }

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getCmp() {
        return cmp;
    }

    public void setCmp(String cmp) {
        this.cmp = cmp;
    }
}

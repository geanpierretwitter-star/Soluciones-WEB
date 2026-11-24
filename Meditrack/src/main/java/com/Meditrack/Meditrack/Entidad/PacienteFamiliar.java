
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "paciente_familiar")
@IdClass(PacienteFamiliar.PacienteFamiliarId.class)
public class PacienteFamiliar {

    @Id
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Id
    @Column(name = "id_familia")
    private Long idFamilia;

    @Column(name = "parentesco", nullable = false, length = 50)
    private String parentesco;

    @Column(name = "autorizado_citas", nullable = false)
    private Boolean autorizadoCitas = false;

    @Column(name = "fecha_vinculo", insertable = false, updatable = false)
    private LocalDateTime fechaVinculo;

    public PacienteFamiliar() {
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Long getIdFamilia() {
        return idFamilia;
    }

    public void setIdFamilia(Long idFamilia) {
        this.idFamilia = idFamilia;
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

    public LocalDateTime getFechaVinculo() {
        return fechaVinculo;
    }

    public void setFechaVinculo(LocalDateTime fechaVinculo) {
        this.fechaVinculo = fechaVinculo;
    }

    public static class PacienteFamiliarId implements Serializable {

        private Long idPaciente;
        private Long idFamilia;

        public PacienteFamiliarId() {
        }

        public PacienteFamiliarId(Long idPaciente, Long idFamilia) {
            this.idPaciente = idPaciente;
            this.idFamilia = idFamilia;
        }

        public Long getIdPaciente() {
            return idPaciente;
        }

        public void setIdPaciente(Long idPaciente) {
            this.idPaciente = idPaciente;
        }

        public Long getIdFamilia() {
            return idFamilia;
        }

        public void setIdFamilia(Long idFamilia) {
            this.idFamilia = idFamilia;
        }

        @Override
        public boolean equals(Object objeto) {
            if (this == objeto) return true;
            if (!(objeto instanceof PacienteFamiliarId otro)) return false;
            return java.util.Objects.equals(idPaciente, otro.idPaciente)
                    && java.util.Objects.equals(idFamilia, otro.idFamilia);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(idPaciente, idFamilia);
        }
    }
}

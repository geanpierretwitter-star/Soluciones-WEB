
package com.Meditrack.Meditrack.Entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_medica")
public class ConsultaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;

    @OneToOne
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    private Cita cita;

    @Column(name = "diagnostico", nullable = false, columnDefinition = "TEXT")
    private String diagnostico;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_consulta", insertable = false, updatable = false)
    private LocalDateTime fechaConsulta;

    public ConsultaMedica() {
    }

    public Long getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Long idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }
}

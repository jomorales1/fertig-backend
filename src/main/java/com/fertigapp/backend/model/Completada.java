package com.fertigapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name="completada")
public class Completada implements Serializable {

    @Id
    @SequenceGenerator(name = "id_completada_generator",
            sequenceName = "public.completada_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_completada_generator")
    @Column(name="id_completada")
    private int id;

    @Column(columnDefinition="DATETIME")
    private OffsetDateTime fecha;

    @Column(name = "fecha_ajustada", columnDefinition="DATETIME")
    private OffsetDateTime fechaAjustada;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="rutina")
    private Rutina rutinaC;

    private boolean hecha;

    public int getId() {
        return id;
    }

    public Rutina getRutinaC() {
        return rutinaC;
    }

    public void setRutinaC(Rutina rutinaC) {
        this.rutinaC = rutinaC;
    }

    public boolean isHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    public OffsetDateTime getFechaAjustada() {
        return fechaAjustada;
    }

    public void setFechaAjustada(OffsetDateTime fechaAjustada) {
        this.fechaAjustada = fechaAjustada;
    }
}

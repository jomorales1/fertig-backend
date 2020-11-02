package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name="completada")
public class Completada implements Serializable {

    @Id
    @SequenceGenerator(name = "id_completada_generator",
            sequenceName = "public.completada_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_completada_generator")
    @Column(name="id_completada")
    private int id;

    private LocalDateTime fecha;

    @Column(name = "fecha_ajustada")
    private LocalDateTime fechaAjustada;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="rutina")
    private Rutina rutinaC;

    private boolean hecha;

    public int getId() {
        return id;
    }

    public Rutina getRutina() {
        return rutinaC;
    }

    public void setRutina(Rutina rutina) {
        this.rutinaC = rutina;
    }

    public boolean isHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaAjustada() {
        return fechaAjustada;
    }

    public void setFechaAjustada(LocalDateTime fechaAjustada) {
        this.fechaAjustada = fechaAjustada;
    }

    public Rutina getRutinaC() {
        return rutinaC;
    }

    public void setRutinaC(Rutina rutinaC) {
        this.rutinaC = rutinaC;
    }
}

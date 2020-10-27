package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="completada")
public class Completada implements Serializable {

    @Id
    @SequenceGenerator(name = "id_completada_generator",
            sequenceName = "public.completada_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_completada_generator")
    @Column(name="id_completada")
    private int id;


    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }

}

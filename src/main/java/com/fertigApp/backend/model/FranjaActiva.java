package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.OffsetTime;

@Entity
@Table(name="franja_libre")
public class FranjaActiva implements Serializable {

    @Id
    @SequenceGenerator(name = "id_fl_generator",
            sequenceName = "public.fl_fl_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_fl_generator")
    @Column(name="id_fl")
    private int id;

    protected int day;

    @Column(name="franja_inicio",columnDefinition = "TIME")
    protected OffsetTime franjaInicio;

    @Column(name="franja_fin",columnDefinition = "TIME")
    protected OffsetTime franjaFin;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario")
    protected Usuario usuarioFL;

    public FranjaActiva() {

    }

    public int getId() {
        return id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public OffsetTime getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(OffsetTime franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public OffsetTime getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(OffsetTime franjaFin) {
        this.franjaFin = franjaFin;
    }

    public Usuario getUsuarioFL() {
        return usuarioFL;
    }

    public void setUsuarioFL(Usuario usuarioFL) {
        this.usuarioFL = usuarioFL;
    }
}

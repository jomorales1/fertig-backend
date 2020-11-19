package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Table(name="franja_libre")
public class FranjaLibre implements Serializable {

    @Id
    @SequenceGenerator(name = "id_fl_generator",
            sequenceName = "public.fl_fl_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_fl_generator")
    @Column(name="id_fl")
    private int id;

    protected int day;

    @Column(name="franja_inicio",columnDefinition = "TIME")
    protected LocalTime franjaInicio;

    @Column(name="franja_fin",columnDefinition = "TIME")
    protected LocalTime franjaFin;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario")
    protected Usuario usuarioFL;

    public FranjaLibre() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public LocalTime getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(LocalTime franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public LocalTime getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(LocalTime franjaFin) {
        this.franjaFin = franjaFin;
    }

    public Usuario getUsuarioFL() {
        return usuarioFL;
    }

    public void setUsuarioFL(Usuario usuarioFL) {
        this.usuarioFL = usuarioFL;
    }
}

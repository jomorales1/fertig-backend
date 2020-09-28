package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="completada", schema="mydb")
public class Completada implements Serializable {

    @Id
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_completada")
    private Rutina rutina;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha")
    private Date fecha;

    public Rutina getRutina() {
        return rutina;
    }

    public void setRutina(Rutina rutina) {
        this.rutina = rutina;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

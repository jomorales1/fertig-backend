package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "amigo")
public class Amigo implements Serializable {

    @EmbeddedId
    private IdAmigo id = new IdAmigo();

//    @ManyToOne
//    @MapsId("agregador")
//    @JoinColumn(name = "agregador")
//    private Usuario agregador;
//
//    @ManyToOne
//    @MapsId("agregado")
//    @JoinColumn(name = "agregado")
//    private Usuario agregado;

    public Amigo() {

    }

}

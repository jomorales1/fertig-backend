package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "sonido")
public class Sonido implements Serializable {

    @Id
    @Column(name="id_sonido")
    @ManyToOne
    private String id;

    public Sonido() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

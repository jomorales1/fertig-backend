package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "sonido")
public class Sonido implements Serializable {

    @Id
    @Column(name="id_sonido")
    private String id;

    @JsonIgnore
    @ManyToMany(mappedBy = "sonidos")
    private List<Usuario> usuarios;

}

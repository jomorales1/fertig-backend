package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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

    public Sonido() {
    }

    public void addUsuario(Usuario usuario) {
        if (this.usuarios == null) {
            this.usuarios = new ArrayList<>();
        }
        this.usuarios.add(usuario);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}

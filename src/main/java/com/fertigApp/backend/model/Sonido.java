package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sonido")
public class Sonido implements Serializable {

    @Id
    @Column(name="id_sonido")
    private String id;

    @JsonIgnore
    @ManyToMany(mappedBy = "sonidos")
    private Set<Usuario> usuarios;

    public void addUsuario(Usuario usuario) {
        if (this.usuarios == null) {
            this.usuarios = new HashSet<>();
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

package com.fertigapp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tarea_de_usuario")
public class TareaDeUsuario implements Serializable {

    @EmbeddedId
    private IdTareaUsuario id = new IdTareaUsuario();

    @ManyToOne
    @MapsId("usuario")
    @JoinColumn(name = "usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("tarea")
    @JoinColumn(name = "tarea")
    private Tarea tarea;

    private boolean admin;

    public IdTareaUsuario getId() {
        return id;
    }

    public void setId(IdTareaUsuario id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Tarea getTarea() {
        return tarea;
    }

    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}

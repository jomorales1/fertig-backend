package com.fertigapp.backend.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdTareaUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private Integer tarea;

    public IdTareaUsuario() {

    }

    public IdTareaUsuario(String usuario, Integer tarea) {
        super();
        this.usuario = usuario;
        this.tarea = tarea;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String idUsuario) {
        this.usuario = idUsuario;
    }

    public Integer getTarea() {
        return tarea;
    }

    public void setTarea(Integer idTarea) {
        this.tarea = idTarea;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
        result = prime * result + ((tarea == null) ? 0 : tarea.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdTareaUsuario other = (IdTareaUsuario) obj;
        return Objects.equals(getUsuario(), other.getUsuario()) && Objects.equals(getTarea(), other.getTarea());
    }
}
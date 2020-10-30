package com.fertigApp.backend.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdTareaUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String idUsuario;
    private Integer idTarea;

    public IdTareaUsuario() {

    }

    public IdTareaUsuario(String idUsuario, Integer idTarea) {
        super();
        this.idUsuario = idUsuario;
        this.idTarea = idTarea;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((idUsuario == null) ? 0 : idUsuario.hashCode());
        result = prime * result + ((idTarea == null) ? 0 : idTarea.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdTareaUsuario other = (IdTareaUsuario) obj;
        return Objects.equals(getIdUsuario(), other.getIdUsuario()) && Objects.equals(getIdTarea(), other.getIdTarea());
    }
}
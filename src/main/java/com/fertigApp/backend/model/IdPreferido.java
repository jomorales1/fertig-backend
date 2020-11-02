package com.fertigApp.backend.model;


import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdPreferido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id_sonido;
    private String usuario;

    public IdPreferido() {

    }

    public IdPreferido(String id_sonido, String usuario) {
        this.id_sonido = id_sonido;
        this.usuario = usuario;
    }

    public String getId_sonido() {
        return id_sonido;
    }

    public void setId_sonido(String id_sonido) {
        this.id_sonido = id_sonido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((usuario == null) ? 0 : usuario.hashCode());
        result = prime * result + ((id_sonido == null) ? 0 : id_sonido.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdPreferido other = (IdPreferido)obj;
        return Objects.equals(getUsuario(), other.getUsuario()) && Objects.equals(getId_sonido(), other.getId_sonido());
    }
}

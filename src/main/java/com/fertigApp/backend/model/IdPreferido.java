package com.fertigApp.backend.model;


import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdPreferido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idSonido;
    private String usuario;

    public IdPreferido() {

    }

    public IdPreferido(String idSonido, String usuario) {
        this.idSonido = idSonido;
        this.usuario = usuario;
    }

    public String getIdSonido() {
        return idSonido;
    }

    public void setIdSonido(String id_sonido) {
        this.idSonido = id_sonido;
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
        result = prime * result + ((idSonido == null) ? 0 : idSonido.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdPreferido other = (IdPreferido)obj;
        return Objects.equals(getUsuario(), other.getUsuario()) && Objects.equals(getIdSonido(), other.getIdSonido());
    }
}

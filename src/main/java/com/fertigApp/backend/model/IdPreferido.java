package com.fertigApp.backend.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class IdPreferido implements Serializable {

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

    public void setIdSonido(String idSonido) {
        this.idSonido = idSonido;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdPreferido that = (IdPreferido) o;
        return idSonido.equals(that.idSonido) &&
                usuario.equals(that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSonido, usuario);
    }
}

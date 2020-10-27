package com.fertigApp.backend.model;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class IdAmigo implements Serializable {

    private String agregador;
    private String agregado;

    public IdAmigo() {

    }

    public IdAmigo(String agregador, String agregado) {
        super();
        this.agregador = agregador;
        this.agregado = agregado;
    }

    public String getAgregador() {
        return agregador;
    }

    public void setAgregador(String agregador) {
        this.agregador = agregador;
    }

    public String getAgregado() {
        return agregado;
    }

    public void setAgregado(String agregado) {
        this.agregado = agregado;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdAmigo idAmigo = (IdAmigo) o;
        return agregador.equals(idAmigo.agregador) &&
                agregado.equals(idAmigo.agregado);
    }

}

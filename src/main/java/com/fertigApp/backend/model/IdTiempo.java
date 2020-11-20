package com.fertigApp.backend.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

public class IdTiempo implements Serializable {

    private TareaDeUsuario tareaDeUsuario;

    private OffsetDateTime fecha;

    public IdTiempo() {

    }

    public IdTiempo(TareaDeUsuario tareaDeUsuario, OffsetDateTime fecha) {
        this.tareaDeUsuario = tareaDeUsuario;
        this.fecha = fecha;
    }

    public TareaDeUsuario getTareaDeUsuario() {
        return tareaDeUsuario;
    }

    public void setTareaDeUsuario(TareaDeUsuario tareaDeUsuario) {
        this.tareaDeUsuario = tareaDeUsuario;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tareaDeUsuario == null) ? 0 : tareaDeUsuario.hashCode());
        result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdTiempo other = (IdTiempo) obj;
        return Objects.equals(getTareaDeUsuario(), other.tareaDeUsuario) && Objects.equals(getFecha(), other.getFecha());
    }
}

package com.fertigApp.backend.model;

import org.checkerframework.checker.units.qual.Time;
import org.hibernate.annotations.Columns;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Objects;

@Embeddable
public class IdTiempo implements Serializable {

    @ManyToOne
    @JoinColumns(value = {
            @JoinColumn(name = "tarea", referencedColumnName = "tarea"),
            @JoinColumn(name = "usuario", referencedColumnName = "usuario")
    })
    private TareaDeUsuario tareaDeUsuario;

    @Column(columnDefinition = "DATETIME")
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
        return Objects.equals(this.tareaDeUsuario, other.tareaDeUsuario) && Objects.equals(getFecha(), other.getFecha());
    }

}

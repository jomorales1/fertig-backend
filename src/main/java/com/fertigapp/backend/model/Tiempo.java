package com.fertigapp.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tiempo")
public class Tiempo implements Serializable {

    @EmbeddedId
    private IdTiempo id = new IdTiempo();

    private Integer invertido;

    public IdTiempo getId() {
        return id;
    }

    public void setId(IdTiempo id) {
        this.id = id;
    }

    public TareaDeUsuario getTareaDeUsuario() {
        return this.id.getTareaDeUsuario();
    }

    public void setTareaDeUsuario(TareaDeUsuario tareaDeUsuario) {
        this.id.setTareaDeUsuario(tareaDeUsuario);
    }

    public OffsetDateTime getFecha() {
        return this.id.getFecha();
    }

    public void setFecha(OffsetDateTime fecha) {
        this.id.setFecha(fecha);
    }

    public Integer getInvertido() {
        return invertido;
    }

    public void setInvertido(Integer invertido) {
        this.invertido = invertido;
    }

}

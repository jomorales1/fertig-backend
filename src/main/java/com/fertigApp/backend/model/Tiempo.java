package com.fertigApp.backend.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

public class Tiempo {

    @EmbeddedId
    private IdTiempo id = new IdTiempo();

    @MapsId("tareaDeUsuario")
    @ManyToOne(optional = false)
    @JoinColumns(value = {
            @JoinColumn(name = "tarea", referencedColumnName = "tarea"),
            @JoinColumn(name = "usuario", referencedColumnName = "usuario")
    })
    private TareaDeUsuario tareaDeUsuario;

    @MapsId("fecha")
    private OffsetDateTime fecha;

    private Integer invertido;
}

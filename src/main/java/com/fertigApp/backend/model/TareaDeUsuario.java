package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tarea_de_usuario")
public class TareaDeUsuario implements Serializable {

    @EmbeddedId
    private IdTareaUsuario id = new IdTareaUsuario();

    @ManyToOne
    @MapsId("usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("tarea")
    private Tarea tarea;

    private boolean admin;

}

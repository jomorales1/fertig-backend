package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "preferido")
public class Preferido implements Serializable {

    @EmbeddedId
    private IdPreferido id = new IdPreferido();

    @ManyToOne
    @MapsId("idSonido")
    @JoinColumn(name = "id_sonido")
    private Sonido idSonido;

    @ManyToOne
    @MapsId("usuario")
    @JoinColumn(name = "usuario")
    private Usuario usuarioP;

    public Preferido() {

    }

    public IdPreferido getId() {
        return id;
    }

    public void setId(IdPreferido id) {
        this.id = id;
    }

    public Sonido getIdSonido() {
        return idSonido;
    }

    public void setIdSonido(Sonido idSonido) {
        this.idSonido = idSonido;
    }

    public Usuario getUsuarioP() {
        return usuarioP;
    }

    public void setUsuarioP(Usuario usuarioP) {
        this.usuarioP = usuarioP;
    }
}

package com.fertigapp.backend.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "preferido")
public class  Preferido implements Serializable{
    @EmbeddedId
    private IdPreferido id = new IdPreferido();

    @ManyToOne
    @MapsId("idSonido")
    @JoinColumn(name = "id_sonido")
    private Sonido sonido;

    @ManyToOne
    @MapsId("usuario")
    @JoinColumn(name = "usuario")
    private Usuario usuario;

    public IdPreferido getId() {
        return id;
    }

    public void setId(IdPreferido id) {
        this.id = id;
    }

    public Sonido getSonido() {
        return sonido;
    }

    public void setSonido(Sonido sonido) {
        this.sonido = sonido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}

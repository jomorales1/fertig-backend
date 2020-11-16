package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "firebase_notification_token")
public class FirebaseNotificationToken implements Serializable {

    @Id
    private String token;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuarioF;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Usuario getUsuarioF() {
        return usuarioF;
    }

    public void setUsuarioF(Usuario usuarioF) {
        this.usuarioF = usuarioF;
    }

}

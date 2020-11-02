package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.IdPreferido;

import java.io.Serializable;

public class RequestSonido implements Serializable {
    private String idSonido;

    public RequestSonido(String idSonido) {
        this.idSonido = idSonido;
    }

    public String getIdSonido() {
        return idSonido;
    }

    public void setIdSonido(String idSonido) {
        this.idSonido = idSonido;
    }
}

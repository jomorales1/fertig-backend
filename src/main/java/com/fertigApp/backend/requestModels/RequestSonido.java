package com.fertigApp.backend.requestModels;

import java.io.Serializable;

public class RequestSonido implements Serializable {

    private String idSonido;

    public RequestSonido() {

    }

    public String getIdSonido() {
        return idSonido;
    }

    public void setIdSonido(String idSonido) {
        this.idSonido = idSonido;
    }

}

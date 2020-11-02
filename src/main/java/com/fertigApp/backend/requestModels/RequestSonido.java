package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.IdPreferido;

public class RequestSonido {
    private String id_sonido;

    public RequestSonido(String id_sonido) {
        this.id_sonido = id_sonido;
    }

    public String getId_sonido() {
        return id_sonido;
    }

    public void setId_sonido(String id_sonido) {
        this.id_sonido = id_sonido;
    }
}

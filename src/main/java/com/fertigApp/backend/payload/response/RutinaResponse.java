package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;


public class RutinaResponse {

    public Completada ultimaCompletada;

    public Rutina rutina;

    public RutinaResponse(Rutina rutina, Completada completada) {
        this.rutina = rutina;
        this.ultimaCompletada = completada;
    }
}

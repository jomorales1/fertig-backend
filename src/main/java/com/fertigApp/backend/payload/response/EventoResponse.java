package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.requestModels.RepeticionEvento;

import java.util.ArrayList;
import java.util.List;

public class EventoResponse {
    private Evento evento;

    private List<RepeticionEvento> repeticiones;

    public EventoResponse(Evento evento) {
        this.evento = evento;
        this.repeticiones = new ArrayList<>();
    }

    public void addRepeticion(RepeticionEvento repeticionEvento){
        this.repeticiones.add(repeticionEvento);
    }


}

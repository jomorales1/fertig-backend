package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Recurrente;
import com.fertigApp.backend.requestModels.RepeticionEvento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventoResponse {
    private Evento evento;

    private List<Date> repeticiones;



    public EventoResponse(Evento evento) {
        this.evento = evento;
        this.repeticiones = Recurrente.findFechas(
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getRecurrencia(),
                true
        );
    }
}
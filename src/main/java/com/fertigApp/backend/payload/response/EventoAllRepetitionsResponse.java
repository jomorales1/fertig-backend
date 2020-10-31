package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;

import java.util.Date;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class EventoAllRepetitionsResponse extends Recurrente {

    private List<Date> repeticiones;

    public EventoAllRepetitionsResponse(){
        super();
    }

    public EventoAllRepetitionsResponse(Evento evento) {
        super(evento);
        this.repeticiones = Recurrente.findFechas(
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getRecurrencia(),
                true
        );
    }
}
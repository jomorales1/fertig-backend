package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;

import java.time.LocalDateTime;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class EventoRepeticionesResponse extends AbstractRecurrenteResponse {

    private List<LocalDateTime> repeticiones;

    public EventoRepeticionesResponse(){
        super();
    }

    public EventoRepeticionesResponse(Evento evento) {
        super(evento);
        this.repeticiones = AbstractRecurrenteResponse.findFechas(
                evento.getFechaInicio(),
                evento.getFechaFin(),
                evento.getRecurrencia()
        );
    }
}
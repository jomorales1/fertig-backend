package com.fertigApp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fertigApp.backend.RecurrentStrategy.EventoRecurrentEntityStrategy;
import com.fertigApp.backend.RecurrentStrategy.RecurrentEntityStrategy;
import com.fertigApp.backend.model.Evento;

import java.time.OffsetDateTime;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class EventoRepeticionesResponse extends AbstractRecurrenteResponse {

    private List<OffsetDateTime> repeticiones;

    @JsonIgnore
    private RecurrentEntityStrategy recurrentEntityStrategy;

    public EventoRepeticionesResponse(){
        super();
    }

    public EventoRepeticionesResponse(Evento evento) {
        super(evento);
        this.recurrentEntityStrategy = new EventoRecurrentEntityStrategy(evento);
        this.repeticiones = recurrentEntityStrategy.findFechas();
    }

    public List<OffsetDateTime> getRepeticiones(){
        return repeticiones;
    }
}
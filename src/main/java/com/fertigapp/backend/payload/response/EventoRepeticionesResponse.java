package com.fertigapp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fertigapp.backend.RecurrentStrategy.EventoRecurrentEntityStrategy;
import com.fertigapp.backend.RecurrentStrategy.RecurrentEntityStrategy;
import com.fertigapp.backend.model.Evento;

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
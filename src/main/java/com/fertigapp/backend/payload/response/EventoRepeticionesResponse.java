package com.fertigapp.backend.payload.response;

import com.fertigapp.backend.model.Evento;

import java.time.OffsetDateTime;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class EventoRepeticionesResponse extends AbstractRecurrenteResponse {

    private List<OffsetDateTime> repeticiones;

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

    public List<OffsetDateTime> getRepeticiones(){
        return repeticiones;
    }
}
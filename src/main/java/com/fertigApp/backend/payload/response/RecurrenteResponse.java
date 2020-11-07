package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Rutina;

import java.time.OffsetDateTime;

//Response con la información y la fecha de la proxima repetición de un evento
public class RecurrenteResponse extends AbstractRecurrenteResponse {

    private OffsetDateTime fecha;

    public RecurrenteResponse(){
        super();
    }

    public RecurrenteResponse(Evento evento) {
        super(evento);
        this.fecha = findSiguiente(evento.getFechaInicio(), evento.getFechaFin(), evento.getRecurrencia());
    }

    public RecurrenteResponse(Rutina rutina, OffsetDateTime fecha) {
        super(rutina);
        this.fecha = fecha;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }
}

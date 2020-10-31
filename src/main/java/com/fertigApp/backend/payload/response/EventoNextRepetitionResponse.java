package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;

import java.util.Date;

//Response con la información y la fecha de la proxima repetición de un evento
public class EventoNextRepetitionResponse extends Recurrente {

    private Date fecha;

    public EventoNextRepetitionResponse(){
        super();
    }

    public EventoNextRepetitionResponse(Evento evento) {
        super(evento);
        this.fecha = findSiguiente(evento.getFechaInicio(), evento.getFechaFin(), evento.getRecurrencia());
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

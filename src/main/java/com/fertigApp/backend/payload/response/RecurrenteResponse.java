package com.fertigApp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.model.Tarea;

import java.time.OffsetDateTime;
import java.util.Set;

//Response con la información y la fecha de la proxima repetición de un evento
public class RecurrenteResponse extends AbstractRecurrenteResponse {

    private OffsetDateTime fecha;

    @JsonInclude
    private Set<Tarea> subtareas;

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
        this.subtareas = rutina.getSubtareas();
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }
}

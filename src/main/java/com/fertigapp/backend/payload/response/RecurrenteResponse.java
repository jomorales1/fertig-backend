package com.fertigapp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Rutina;
import com.fertigapp.backend.model.Tarea;

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
        this.fecha = findSiguiente(evento.getFechaInicio(), evento.getFechaFin(), evento.getRecurrencia(), evento.getFechaInicio());
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

    public Set<Tarea> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(Set<Tarea> subtareas) {
        this.subtareas = subtareas;
    }

}

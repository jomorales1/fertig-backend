package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Rutina;

import java.time.LocalDateTime;

//Response con la información y la fecha de la proxima repetición de un evento
public class RecurrenteResponse extends AbstractRecurrenteResponse {

    private LocalDateTime fecha;

    public RecurrenteResponse(){
        super();
    }

    public RecurrenteResponse(Evento evento) {
        super(evento);
        this.fecha = findSiguiente(evento.getFechaInicio(), evento.getFechaFin(), evento.getRecurrencia());
    }

    public RecurrenteResponse(Rutina rutina, LocalDateTime fecha) {
        super(rutina);
        this.fecha = fecha;
//        if(rutina.getRecurrencia().charAt(0)=='H')
//            this.fecha = findSiguiente(rutina.getFechaInicio(), rutina.getFechaFin(), rutina.getRecurrencia(), rutina.getDuracion(), rutina.getFranjaInicio(), rutina.getFranjaFin());
//        else
//            this.fecha =  findSiguiente(rutina.getFechaInicio(), rutina.getFechaFin(), rutina.getRecurrencia());
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}

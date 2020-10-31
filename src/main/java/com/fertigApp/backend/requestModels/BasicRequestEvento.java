package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Recurrente;

import java.util.Date;

public class BasicRequestEvento extends Recurrente {

    private Date fecha;

    public BasicRequestEvento(){
        super();
    }

    public BasicRequestEvento(Evento evento) {
        super(evento);
        this.fecha = findFechas(evento.getFechaInicio(), evento.getFechaFin(), evento.getRecurrencia(), true).get(0);
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

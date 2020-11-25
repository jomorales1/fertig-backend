package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.RecurrenceStrategy.*;
import com.fertigApp.backend.model.Evento;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class EventoRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Evento evento;

    private RecurrenceStrategy recurrenceStrategy;

    public EventoRecurrentEntityStrategy(Evento evento){
        this.evento = evento;
        if(evento.getRecurrencia() == null){
            recurrenceStrategy = new NullStrategy(evento.getFechaFin());
        }
        switch (evento.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy(evento.getRecurrencia());
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(evento.getRecurrencia());
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(evento.getRecurrencia());
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(evento.getRecurrencia());
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(evento.getRecurrencia());
                break;
            case 'E':
                recurrenceStrategy = new EStrategy(evento.getRecurrencia());
                break;
        }
    }

    @Override
    public List<OffsetDateTime> findFechas() {

        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime currentDate = fechaInicio;

        LinkedList<OffsetDateTime> fechas = new LinkedList<>();

        while(currentDate != null){
            fechas.add(currentDate);
            currentDate = findSiguiente(currentDate);
        }

        return fechas;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentTime) {
        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(currentTime.isBefore(fechaInicio)){
            return fechaInicio;
        }

        OffsetDateTime nextDate = recurrenceStrategy.add(currentTime);
        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    @Override
    public OffsetDateTime findAnterior(OffsetDateTime currentTime) {
        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(fechaFin.isBefore(currentTime)){
            return fechaFin;
        }

        OffsetDateTime previous = recurrenceStrategy.minus(currentTime);
        if(previous.isBefore(fechaInicio)){
            return null;
        }
        return previous;
    }
}

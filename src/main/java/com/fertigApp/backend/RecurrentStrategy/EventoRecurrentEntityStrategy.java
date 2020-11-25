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
            currentDate = findNextFromValidDate(currentDate);
        }

        return fechas;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentDate) {
        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime nextDate = OffsetDateTime.from(fechaInicio);

        while(nextDate != null && nextDate.isBefore(currentDate)){
            nextDate = findNextFromValidDate(nextDate);
        }

        return nextDate;
    }

    @Override
    public OffsetDateTime findAnterior(OffsetDateTime currentDate) {
        OffsetDateTime nextDate = findSiguiente(currentDate);
        return  nextDate == null ? nextDate : findPreviousFromValidDate(nextDate);
    }

    @Override
    public RecurrenceStrategy getRecurrenceStrategy() {
        return recurrenceStrategy;
    }

    private OffsetDateTime findNextFromValidDate(OffsetDateTime currentDate){
        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(currentDate.isBefore(fechaInicio)){
            return fechaInicio;
        }

        OffsetDateTime nextDate = recurrenceStrategy.add(currentDate);
        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    private OffsetDateTime findPreviousFromValidDate(OffsetDateTime currentDate){
        OffsetDateTime fechaInicio = evento.getFechaInicio();
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(fechaFin.isBefore(currentDate)){
            return fechaFin;
        }

        OffsetDateTime previous = recurrenceStrategy.minus(currentDate);
        if(previous.isBefore(fechaInicio)){
            return null;
        }
        return previous;
    }
}

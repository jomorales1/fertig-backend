package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.RecurrenceStrategy.*;
import com.fertigApp.backend.model.Evento;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class EventoRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Evento evento;

    private RecurrenceStrategy recurrenceStrategy;

    private OffsetDateTime firstValidDate;

    public EventoRecurrentEntityStrategy(Evento evento){
        this.evento = evento;
        if(evento.getRecurrencia() == null){
            recurrenceStrategy = new NullStrategy(evento.getFechaFin());
        }
        switch (evento.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy(evento.getRecurrencia());
                firstValidDate = evento.getFechaInicio();
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(evento.getRecurrencia());
                firstValidDate = evento.getFechaInicio();
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(evento.getRecurrencia());
                firstValidDate = evento.getFechaInicio();
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(evento.getRecurrencia());
                firstValidDate = evento.getFechaInicio();
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(evento.getRecurrencia());
                firstValidDate = evento.getFechaInicio();
                break;
            case 'E':
                EStrategy eStrategy = new EStrategy(evento.getRecurrencia());
                boolean []recurrenceDays = (eStrategy).getRecurrenceDays();
                if (recurrenceDays[evento.getFechaInicio().getDayOfWeek().getValue() - 1]) {
                    firstValidDate = evento.getFechaInicio();
                } else {
                    firstValidDate = eStrategy.add(evento.getFechaInicio());
                }
                recurrenceStrategy = eStrategy;
                break;
        }
    }

    @Override
    public List<OffsetDateTime> findFechas() {
        OffsetDateTime currentDate = OffsetDateTime.from(firstValidDate);

        LinkedList<OffsetDateTime> fechas = new LinkedList<>();

        while(currentDate != null){
            fechas.add(currentDate);
            currentDate = findNextFromValidDate(currentDate);
        }

        return fechas;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentDate) {
        OffsetDateTime nextDate = OffsetDateTime.from(firstValidDate);

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
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(currentDate.isBefore(firstValidDate)){
            return firstValidDate;
        }

        OffsetDateTime nextDate = recurrenceStrategy.add(currentDate);
        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    private OffsetDateTime findPreviousFromValidDate(OffsetDateTime currentDate){
        OffsetDateTime fechaFin = evento.getFechaFin();
        if(fechaFin.isBefore(currentDate)){
            return fechaFin;
        }

        OffsetDateTime previous = recurrenceStrategy.minus(currentDate);
        if(previous.isBefore(firstValidDate)){
            return null;
        }
        return previous;
    }
}

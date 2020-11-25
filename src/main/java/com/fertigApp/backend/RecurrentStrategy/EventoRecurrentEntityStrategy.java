package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.RecurrenceStrategy.*;
import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.services.EventoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class EventoRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Evento evento;

    private RecurrenceStrategy recurrenceStrategy;

    public EventoRecurrentEntityStrategy(Evento evento){
        this.evento = evento;
        switch (evento.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy();
                break;
            case 'D':
                recurrenceStrategy = new DStrategy();
                break;
            case 'S':
                recurrenceStrategy = new WStrategy();
                break;
            case 'M':
                recurrenceStrategy = new MStrategy();
                break;
            case 'A':
                recurrenceStrategy = new YStrategy();
                break;
            case 'E':
                recurrenceStrategy = new EStrategy();
                break;
        }
        recurrenceStrategy.set(evento.getRecurrencia());
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

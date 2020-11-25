package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.RecurrenceStrategy.*;
import com.fertigApp.backend.model.Rutina;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.LinkedList;
import java.util.List;

public class RutinaRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Rutina rutina;

    private RecurrenceStrategy recurrenceStrategy;
    private RecurrenceStrategy franjaStrategy;

    public RutinaRecurrentEntityStrategy(Rutina rutina){
        this.rutina = rutina;
        switch (rutina.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D1");
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D0");
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D0");
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D0");
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D0");
                break;
            case 'E':
                recurrenceStrategy = new EStrategy(rutina.getRecurrencia());
                franjaStrategy = new DStrategy("D0");
                break;
        }
    }

    @Override
    public List<OffsetDateTime> findFechas() {
        OffsetDateTime fechaInicio = rutina.getFechaInicio();
        OffsetDateTime currentDate = OffsetDateTime.from(fechaInicio);

        LinkedList<OffsetDateTime> fechas = new LinkedList<>();

        while(currentDate != null){
            fechas.add(currentDate);
            currentDate = findSiguiente(currentDate);
        }

        return fechas;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentTime) {
        OffsetDateTime fechaInicio = rutina.getFechaInicio();
        OffsetDateTime fechaFin = rutina.getFechaFin();
        OffsetTime franjaInicio = rutina.getFranjaInicio();
        OffsetTime franjaFin = rutina.getFranjaFin();

        if(currentTime.isBefore(fechaInicio)){
            return fechaInicio;
        }

        OffsetDateTime nextDate = OffsetDateTime.from(currentTime);
        if(franjaInicio == null){
            nextDate = recurrenceStrategy.add(nextDate);
        } else {
            do {
                nextDate = recurrenceStrategy.add(nextDate);
            }while(nextDate.isBefore(franjaInicio.atDate(nextDate.toLocalDate())) && nextDate.isAfter(franjaFin.atDate(nextDate.toLocalDate())));
        }

        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    @Override
    public OffsetDateTime findAnterior(OffsetDateTime currentTime) {
        OffsetDateTime fechaInicio = rutina.getFechaInicio();
        OffsetDateTime fechaFin = rutina.getFechaFin();
        OffsetTime franjaInicio = rutina.getFranjaInicio();
        OffsetTime franjaFin = rutina.getFranjaFin();

        if(currentTime.isAfter(fechaFin)){
            return fechaFin;
        }

        OffsetDateTime nextDate = OffsetDateTime.from(currentTime);
        if(franjaInicio == null){
            nextDate = recurrenceStrategy.minus(nextDate);
        } else {
            do {
                nextDate = recurrenceStrategy.minus(nextDate);
            }while(nextDate.isBefore(franjaInicio.atDate(nextDate.toLocalDate())) && nextDate.isAfter(franjaFin.atDate(nextDate.toLocalDate())));
        }

        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }
}

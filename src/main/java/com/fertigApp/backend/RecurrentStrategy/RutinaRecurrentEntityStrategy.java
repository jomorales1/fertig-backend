package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.RecurrenceStrategy.*;
import com.fertigApp.backend.model.Rutina;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

public class RutinaRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Rutina rutina;

    private RecurrenceStrategy recurrenceStrategy;

    public RutinaRecurrentEntityStrategy(Rutina rutina){
        this.rutina = rutina;
        switch (rutina.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy(rutina.getRecurrencia());
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(rutina.getRecurrencia());
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(rutina.getRecurrencia());
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(rutina.getRecurrencia());
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(rutina.getRecurrencia());
                break;
            case 'E':
                recurrenceStrategy = new EStrategy(rutina.getRecurrencia());
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
            currentDate = findNextFromValidDate(currentDate);
        }

        return fechas;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentDate) {
        OffsetDateTime fechaInicio = rutina.getFechaInicio();
        OffsetDateTime nextDate = OffsetDateTime.from(fechaInicio);

        while(nextDate != null && nextDate.isBefore(currentDate)){
            nextDate = findNextFromValidDate(nextDate);
        }

        return nextDate;
    }

    @Override
    public OffsetDateTime findAnterior(OffsetDateTime currentDate){
        return findPreviousFromValidDate(findSiguiente(currentDate));
    }

    @Override
    public RecurrenceStrategy getRecurrenceStrategy() {
        return recurrenceStrategy;
    }

    private OffsetDateTime findNextFromValidDate(OffsetDateTime currentDate) {
        OffsetDateTime fechaInicio = rutina.getFechaInicio();
        OffsetDateTime fechaFin = rutina.getFechaFin();

        if(currentDate.isBefore(fechaInicio)){
            return fechaInicio;
        }

        OffsetDateTime nextDate = OffsetDateTime.from(currentDate);
        if(rutina.getFranjaInicio() == null){
            nextDate = recurrenceStrategy.add(nextDate);
        } else {
            OffsetDateTime franjaInico = currentDate.toLocalDate().atTime(rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC));
            OffsetDateTime franjaFin = currentDate.toLocalDate().atTime(rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));

            if(franjaInico.isAfter(franjaFin))
                franjaFin = franjaFin.plusDays(1);

            do{
                nextDate = recurrenceStrategy.add(nextDate);
                if(nextDate.isAfter(franjaFin)){
                    franjaInico = franjaInico.plusDays(1);
                    franjaFin = franjaFin.plusDays(1);
                }
            } while(nextDate.isBefore(franjaInico));
        }

        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    private OffsetDateTime findPreviousFromValidDate(OffsetDateTime currentDate) {
        OffsetDateTime fechaFin = rutina.getFechaFin();
        OffsetDateTime fechaInicio = rutina.getFechaInicio();

        if(currentDate.isAfter(fechaFin)){
            return fechaFin;
        }

        OffsetDateTime previousDate = OffsetDateTime.from(currentDate);
        if(rutina.getFranjaInicio() == null){
            previousDate = recurrenceStrategy.add(previousDate);
        } else {
            OffsetDateTime franjaFin = currentDate.toLocalDate().atTime(rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));
            OffsetDateTime franjaInico = currentDate.toLocalDate().atTime(rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC));

            if(franjaInico.isAfter(franjaFin))
                franjaFin = franjaFin.minusDays(1);

            do{
                previousDate = recurrenceStrategy.minus(previousDate);
                if(previousDate.isBefore(franjaInico)){
                    franjaInico = franjaInico.minusDays(1);
                    franjaFin = franjaFin.minusDays(1);
                }
            } while(previousDate.isAfter(franjaFin));
        }

        if(!previousDate.isAfter(fechaInicio)){
            return null;
        }
        return previousDate;
    }
}

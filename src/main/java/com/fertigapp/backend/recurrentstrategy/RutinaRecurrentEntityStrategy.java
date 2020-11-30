package com.fertigapp.backend.recurrentstrategy;

import com.fertigapp.backend.recurrencestrategy.*;
import com.fertigapp.backend.model.Rutina;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;

public class RutinaRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Rutina rutina;

    private RecurrenceStrategy recurrenceStrategy;

    private OffsetDateTime firstValidDate;

    public RutinaRecurrentEntityStrategy(Rutina rutina){
        this.rutina = rutina;
        switch (rutina.getRecurrencia().charAt(0)){
            case 'H':
                recurrenceStrategy = new HStrategy(rutina.getRecurrencia());
                OffsetDateTime fechaInicio = rutina.getFechaInicio();
                firstValidDate = fechaInicio;
                if(rutina.getFranjaInicio() != null){
                    OffsetDateTime franjaInico = fechaInicio.toLocalDate().atTime(rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC));
                    OffsetDateTime franjaFin = fechaInicio.toLocalDate().atTime(rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));

                    if(franjaInico.isAfter(franjaFin))
                        franjaFin = franjaFin.plusDays(1);

                    if (fechaInicio.isBefore(franjaInico) || fechaInicio.isAfter(franjaFin))
                        firstValidDate = findNextFromValidDate(fechaInicio);
                }
                break;
            case 'D':
                recurrenceStrategy = new DStrategy(rutina.getRecurrencia());
                firstValidDate = rutina.getFechaInicio();
                break;
            case 'S':
                recurrenceStrategy = new WStrategy(rutina.getRecurrencia());
                firstValidDate = rutina.getFechaInicio();
                break;
            case 'M':
                recurrenceStrategy = new MStrategy(rutina.getRecurrencia());
                firstValidDate = rutina.getFechaInicio();
                break;
            case 'A':
                recurrenceStrategy = new YStrategy(rutina.getRecurrencia());
                firstValidDate = rutina.getFechaInicio();
                break;
            default:
                EStrategy eStrategy = new EStrategy(rutina.getRecurrencia());
                boolean []recurrenceDays = (eStrategy).getRecurrenceDays();
                if (recurrenceDays[rutina.getFechaInicio().getDayOfWeek().getValue() - 1]) {
                    firstValidDate = rutina.getFechaInicio();
                } else {
                    firstValidDate = eStrategy.add(rutina.getFechaInicio());
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
    public OffsetDateTime findAnterior(OffsetDateTime currentDate){
        return findPreviousFromValidDate(findSiguiente(currentDate));
    }

    @Override
    public RecurrenceStrategy getRecurrenceStrategy() {
        return recurrenceStrategy;
    }

    private OffsetDateTime findNextFromValidDate(OffsetDateTime currentDate) {
        OffsetDateTime fechaFin = rutina.getFechaFin();

        if(currentDate.isBefore(firstValidDate)){
            return firstValidDate;
        }

        OffsetDateTime nextDate = OffsetDateTime.from(currentDate);
        if(rutina.getFranjaInicio() == null){
            nextDate = recurrenceStrategy.add(nextDate);
        } else {
            OffsetDateTime franjaInicio = currentDate.toLocalDate().atTime(rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC));
            OffsetDateTime franjaFin = currentDate.toLocalDate().atTime(rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));

            if(franjaInicio.isAfter(franjaFin))
                franjaFin = franjaFin.plusDays(1);

            do{
                nextDate = recurrenceStrategy.add(nextDate);
                if(nextDate.isAfter(franjaFin)){
                    franjaInicio = franjaInicio.plusDays(1);
                    franjaFin = franjaFin.plusDays(1);
                }
            } while(nextDate.isBefore(franjaInicio));
        }

        if(!nextDate.isBefore(fechaFin)){
            return null;
        }
        return nextDate;
    }

    private OffsetDateTime findPreviousFromValidDate(OffsetDateTime currentDate) {
        OffsetDateTime fechaFin = rutina.getFechaFin();

        if(currentDate.isAfter(fechaFin)){
            return fechaFin;
        }

        OffsetDateTime previousDate = OffsetDateTime.from(currentDate);
        if(rutina.getFranjaInicio() == null){
            previousDate = recurrenceStrategy.minus(previousDate);
        } else {
            OffsetDateTime franjaFin = currentDate.toLocalDate().atTime(rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));
            OffsetDateTime franjaInicio = currentDate.toLocalDate().atTime(rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC));

            if(franjaInicio.isAfter(franjaFin))
                franjaFin = franjaFin.minusDays(1);

            do{
                previousDate = recurrenceStrategy.minus(previousDate);
                if(previousDate.isBefore(franjaInicio)){
                    franjaInicio = franjaInicio.minusDays(1);
                    franjaFin = franjaFin.minusDays(1);
                }
            } while(previousDate.isAfter(franjaInicio));
        }

        if(!previousDate.isAfter(firstValidDate)){
            return null;
        }
        return previousDate;
    }
}

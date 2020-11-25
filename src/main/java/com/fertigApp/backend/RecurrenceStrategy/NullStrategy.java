package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class NullStrategy implements RecurrenceStrategy {

    private OffsetDateTime fechaFin;

    public NullStrategy(OffsetDateTime fechaFin){
        this.fechaFin = fechaFin;
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return fechaFin;
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return fechaFin;
    }
}

package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class DStrategy implements RecurrenceStrategy{

    private int n;

    public DStrategy(String recurrence){
        this.n = Integer.parseInt(recurrence.substring(1));
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusDays(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusDays(n), currentDate.getOffset());
    }
}

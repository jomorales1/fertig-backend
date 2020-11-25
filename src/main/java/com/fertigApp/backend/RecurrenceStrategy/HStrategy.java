package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class HStrategy implements RecurrenceStrategy{

    private int n;

    public HStrategy(String recurrence){
        this.n = Integer.parseInt(recurrence.substring(1));
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusHours(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusHours(n), currentDate.getOffset());
    }
}

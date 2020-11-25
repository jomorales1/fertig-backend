package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class WStrategy implements RecurrenceStrategy{

    private int n;

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusWeeks(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusWeeks(n), currentDate.getOffset());
    }

    @Override
    public void set(String recurrence) {
        this.n = Integer.parseInt(recurrence.substring(1));
    }
}

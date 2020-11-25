package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class MStrategy implements RecurrenceStrategy{

    private int n;

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusMonths(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusMonths(n), currentDate.getOffset());
    }

    @Override
    public void set(String recurrence) {
        this.n = Integer.parseInt(recurrence.substring(1));
    }
}

package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class YStrategy implements RecurrenceStrategy{

    private int n;

    public YStrategy(String recurrence){
        this.n = Integer.parseInt(recurrence.substring(1));
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusYears(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusYears(n), currentDate.getOffset());
    }
}

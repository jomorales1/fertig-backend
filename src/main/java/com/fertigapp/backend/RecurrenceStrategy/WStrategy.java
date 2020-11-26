package com.fertigapp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class WStrategy implements RecurrenceStrategy{

    private int n;

    public WStrategy(String recurrence){
        this.n = Integer.parseInt(recurrence.substring(1));
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusWeeks(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusWeeks(n), currentDate.getOffset());
    }

    @Override
    public String getRecurrenceMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Cada ");
        if(n > 1){
            message.append(n);
            message.append("semanas.");
        } else {
            message.append("semana.");
        }
        return message.toString();
    }
}

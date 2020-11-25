package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public class MStrategy implements RecurrenceStrategy{

    private int n;

    public MStrategy(String recurrence){
        this.n = Integer.parseInt(recurrence.substring(1));
    }

    @Override
    public OffsetDateTime add(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().plusMonths(n), currentDate.getOffset());
    }

    @Override
    public OffsetDateTime minus(OffsetDateTime currentDate) {
        return OffsetDateTime.of(currentDate.toLocalDateTime().minusMonths(n), currentDate.getOffset());
    }

    @Override
    public String getRecurrenceMessage() {
        StringBuilder message = new StringBuilder();
        message.append("Cada ");
        if(n > 1){
            message.append(n);
            message.append("meses.");
        } else {
            message.append("mes.");
        }
        return message.toString();
    }
}

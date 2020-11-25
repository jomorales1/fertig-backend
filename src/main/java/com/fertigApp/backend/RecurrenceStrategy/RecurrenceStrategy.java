package com.fertigApp.backend.RecurrenceStrategy;

import java.time.OffsetDateTime;

public interface RecurrenceStrategy {
    OffsetDateTime add(OffsetDateTime currentDate);
    OffsetDateTime minus(OffsetDateTime currentDate);
    String getRecurrenceMessage();
}

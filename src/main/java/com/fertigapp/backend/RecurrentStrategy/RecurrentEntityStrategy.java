package com.fertigapp.backend.RecurrentStrategy;

import com.fertigapp.backend.RecurrenceStrategy.RecurrenceStrategy;

import java.time.OffsetDateTime;
import java.util.List;

public interface RecurrentEntityStrategy {
    List<OffsetDateTime> findFechas();
    OffsetDateTime findSiguiente(OffsetDateTime currentDate);
    OffsetDateTime findAnterior(OffsetDateTime currentDate);
    RecurrenceStrategy getRecurrenceStrategy();
}

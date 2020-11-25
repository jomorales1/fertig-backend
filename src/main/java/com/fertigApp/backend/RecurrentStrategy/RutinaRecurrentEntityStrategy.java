package com.fertigApp.backend.RecurrentStrategy;

import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.services.RutinaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public class RutinaRecurrentEntityStrategy implements RecurrentEntityStrategy {

    private Rutina rutina;

    public RutinaRecurrentEntityStrategy(Rutina rutina){
        this.rutina = rutina;
    }

    @Override
    public List<OffsetDateTime> findFechas() {
        return null;
    }

    @Override
    public OffsetDateTime findSiguiente(OffsetDateTime currentTime) {
        return null;
    }

    @Override
    public OffsetDateTime findAnterior(OffsetDateTime currentTime) {
        return null;
    }
}

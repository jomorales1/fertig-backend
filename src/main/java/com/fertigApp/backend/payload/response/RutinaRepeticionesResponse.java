package com.fertigApp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigApp.backend.RecurrentStrategy.RecurrentEntityStrategy;
import com.fertigApp.backend.RecurrentStrategy.RutinaRecurrentEntityStrategy;
import com.fertigApp.backend.model.Rutina;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class RutinaRepeticionesResponse extends AbstractRecurrenteResponse  {

    @JsonInclude
    private List<OffsetDateTime> completadas;
    @JsonInclude
    private List<OffsetDateTime> futuras;

    @JsonIgnore
    private RecurrentEntityStrategy recurrentEntityStrategy;

    public RutinaRepeticionesResponse() {
        super();
    }

    public RutinaRepeticionesResponse(Rutina rutina, List<OffsetDateTime> completadas, OffsetDateTime maxAjustada) {
        super(rutina);
        this.completadas = completadas;
        this.recurrentEntityStrategy = new RutinaRecurrentEntityStrategy(rutina);
        futuras =  recurrentEntityStrategy.findFechas();
    }

    public List<OffsetDateTime> getCompletadas() {
        return completadas;
    }

    public List<OffsetDateTime> getFuturas() {
        return futuras;
    }
}
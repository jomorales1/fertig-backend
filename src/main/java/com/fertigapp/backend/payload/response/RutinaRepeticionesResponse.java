package com.fertigapp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigapp.backend.model.Rutina;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class RutinaRepeticionesResponse extends AbstractRecurrenteResponse  {

    @JsonInclude
    private List<OffsetDateTime> completadas;
    @JsonInclude
    private List<OffsetDateTime> futuras;

    public RutinaRepeticionesResponse(){
        super();
    }

    public RutinaRepeticionesResponse(Rutina rutina, List<OffsetDateTime> completadas, OffsetDateTime maxAjustada) {
        super(rutina);
        this.completadas = completadas;
        OffsetDateTime fechaIdeal = (completadas.isEmpty()) ? rutina.getFechaInicio() : completadas.get(completadas.size()-1);
        if(rutina.getRecurrencia().charAt(0) == 'H')
            this.futuras = AbstractRecurrenteResponse.findFechas(fechaIdeal, rutina.getFechaFin(), rutina.getRecurrencia(), rutina.getDuracion(), rutina.getFranjaInicio().withOffsetSameLocal(ZoneOffset.UTC), rutina.getFranjaFin().withOffsetSameLocal(ZoneOffset.UTC));
        else
            this.futuras = AbstractRecurrenteResponse.findFechas(fechaIdeal, rutina.getFechaFin(), rutina.getRecurrencia());
        //completadas: select de las completadas hechas
        //futuras: fechas: inicio: fechaideal de la ultima completada fin: la del front
    }

    public List<OffsetDateTime> getCompletadas() {
        return completadas;
    }

    public List<OffsetDateTime> getFuturas() {
        return futuras;
    }
}
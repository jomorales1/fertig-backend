package com.fertigApp.backend.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigApp.backend.model.Rutina;

import java.time.LocalDateTime;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class RutinaRepeticionesResponse extends AbstractRecurrenteResponse  {

    @JsonInclude
    private List<LocalDateTime> completadas;
    @JsonInclude
    private List<LocalDateTime> futuras;

    public RutinaRepeticionesResponse(){
        super();
    }

    public RutinaRepeticionesResponse(Rutina rutina, List<LocalDateTime> completadas, LocalDateTime maxAjustada) {
        super(rutina);
        this.completadas = completadas;
        LocalDateTime fechaIdeal = (completadas.isEmpty()) ? rutina.getFechaInicio() : completadas.get(completadas.size()-1);
        if(rutina.getRecurrencia().charAt(0) == 'H')
            this.futuras = AbstractRecurrenteResponse.findFechas(fechaIdeal, rutina.getFechaFin(), rutina.getRecurrencia(), rutina.getDuracion(), rutina.getFranjaInicio(), rutina.getFranjaFin());
        else
            this.futuras = AbstractRecurrenteResponse.findFechas(fechaIdeal, rutina.getFechaFin(), rutina.getRecurrencia());
        //completadas: select de las completadas hechas
        //futuras: fechas: inicio: fechaideal de la ultima completada fin: la del front
    }

    public List<LocalDateTime> getCompletadas() {
        return completadas;
    }

    public List<LocalDateTime> getFuturas() {
        return futuras;
    }
}
package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;

import java.util.Date;
import java.util.List;

//Response con la información de un evento y las fechas de todas sus repeticiones
public class RutinaRepeticionesResponse extends AbstractRecurrenteResponse {

    private List<Date> completadas;
    private List<Date> futuras;

    public RutinaRepeticionesResponse(){
        super();
    }

    public RutinaRepeticionesResponse(Evento evento) {
        super(evento);
        //completadas: select de las completadas hechas
        //futuras: fechas: inicio: fechaideal de la ultima completada fin: la del front
    }
}
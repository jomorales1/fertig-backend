package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Rutina;
import com.fertigApp.backend.services.CompletadaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RutinaResponse {

    public Completada ultimaCompletada;

    public Rutina rutina;

    @Autowired
    CompletadaService completadaService;

    public RutinaResponse(Rutina rutina) {
        this.rutina = rutina;
        List<Completada> completadas = (List<Completada>) completadaService.findByRutina(rutina);
        this.ultimaCompletada = completadas.get(completadas.size()-1);
    }
}

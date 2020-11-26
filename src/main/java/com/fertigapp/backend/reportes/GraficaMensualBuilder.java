package com.fertigapp.backend.reportes;

import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.services.CompletadaService;
import com.fertigapp.backend.services.TareaService;
import com.fertigapp.backend.services.TiempoService;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class GraficaMensualBuilder implements GraficaBuilder{

    private final TareaService tareaService;
    private final CompletadaService completadaService;
    private final TiempoService tiempoService;

    public GraficaMensualBuilder(TareaService tareaService, CompletadaService completadaService, TiempoService tiempoService) {
        this.tareaService = tareaService;
        this.completadaService = completadaService;
        this.tiempoService = tiempoService;
    }

    @Override
    public Grafica crearGrafica(OffsetDateTime fecha, Usuario usuario) {
        OffsetDateTime inicio = OffsetDateTime.of(fecha.getYear(),fecha.getMonthValue(),1,0,0,0,0,fecha.getOffset());
        OffsetDateTime fin = OffsetDateTime.from(inicio).plusMonths(1);
        List<Integer> minutos = new LinkedList<>();
        List<Integer> tareas = new LinkedList<>();
        List<OffsetDateTime> fechas = new LinkedList<>();
        for (OffsetDateTime i = OffsetDateTime.from(inicio); i.isBefore(fin); i = i.plusDays(1)){
            fechas.add(i);
            Integer a = tiempoService.countTiempoTareaBetween(i,i.plusDays(1),usuario);
            Integer b = completadaService.countTiempoCompletadasBetween(i,i.plusDays(1),usuario);
            tareas.add(tareaService.countTareasBetween(i,i.plusDays(1),usuario)+completadaService.countCompletadasBetween(i,i.plusDays(1),usuario));
            minutos.add((b == null ? 0 : b) + (a == null ? 0 : a));
        }
        return new GraficaMensual(fechas, minutos, tareas);
    }
}

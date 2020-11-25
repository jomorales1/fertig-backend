package com.fertigApp.backend.reportes;

import com.fertigApp.backend.model.Usuario;
import com.fertigApp.backend.services.CompletadaService;
import com.fertigApp.backend.services.TareaService;
import com.fertigApp.backend.services.TiempoService;

import java.time.OffsetDateTime;

public class ReporteAnualBuilder implements ReporteBuilder{
    private final TareaService tareaService;
    private final CompletadaService completadaService;
    private final TiempoService tiempoService;

    public ReporteAnualBuilder(TareaService tareaService, CompletadaService completadaService, TiempoService tiempoService) {
        this.tareaService = tareaService;
        this.completadaService = completadaService;
        this.tiempoService = tiempoService;
    }

    @Override
    public Reporte crearReporte(OffsetDateTime fecha, Usuario usuario) {
        OffsetDateTime inicio = OffsetDateTime.of(fecha.getYear(),1,1,0,0,0,0,fecha.getOffset());
        OffsetDateTime fin = OffsetDateTime.from(inicio).plusYears(1);
        Integer a = tiempoService.countTiempoTareaBetween(inicio,fin,usuario);
        Integer b = completadaService.countTiempoCompletadasBetween(inicio,fin,usuario);
        return new ReporteAnual(
                tareaService.countTareasBetween(inicio,fin,usuario)+completadaService.countCompletadasBetween(inicio,fin,usuario),
                (b == null ? 0 : b) + (a == null ? 0 : a)
        );
    }
}



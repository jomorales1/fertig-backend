package com.fertigapp.backend.reportes;

import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.services.CompletadaService;
import com.fertigapp.backend.services.TareaService;
import com.fertigapp.backend.services.TiempoService;

import java.time.OffsetDateTime;

public class ReporteSemanalBuilder implements ReporteBuilder{
    private final TareaService tareaService;
    private final CompletadaService completadaService;
    private final TiempoService tiempoService;

    public ReporteSemanalBuilder(TareaService tareaService, CompletadaService completadaService, TiempoService tiempoService) {
        this.tareaService = tareaService;
        this.completadaService = completadaService;
        this.tiempoService = tiempoService;
    }

    @Override
    public Reporte crearReporte(OffsetDateTime fecha, Usuario usuario) {
        OffsetDateTime inicio = OffsetDateTime.of(fecha.getYear(),fecha.getMonthValue(),fecha.getDayOfMonth(),0,0,0,0,fecha.getOffset()).minusDays(fecha.getDayOfWeek().getValue());
        OffsetDateTime fin = OffsetDateTime.from(inicio).plusWeeks(1);
        Integer a = tiempoService.countTiempoTareaBetween(inicio,fin,usuario);
        Integer b = completadaService.countTiempoCompletadasBetween(inicio,fin,usuario);
        return new ReporteSemanal(
                tareaService.countTareasBetween(inicio,fin,usuario)+completadaService.countCompletadasBetween(inicio,fin,usuario),
                (b == null ? 0 : b) + (a == null ? 0 : a)
        );
    }
}

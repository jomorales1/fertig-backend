package com.fertigapp.backend.reportes;

import com.fertigapp.backend.model.Usuario;
import com.fertigapp.backend.services.CompletadaService;
import com.fertigapp.backend.services.TareaService;
import com.fertigapp.backend.services.TiempoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

public class ReporteMensualBuilder implements ReporteBuilder{

    private static final Logger LOGGER = LoggerFactory.getLogger(ReporteMensualBuilder.class);

    private final TareaService tareaService;
    private final CompletadaService completadaService;
    private final TiempoService tiempoService;

    public ReporteMensualBuilder(TareaService tareaService, CompletadaService completadaService, TiempoService tiempoService) {
        this.tareaService = tareaService;
        this.completadaService = completadaService;
        this.tiempoService = tiempoService;
    }

    @Override
    public Reporte crearReporte(OffsetDateTime fecha, Usuario usuario) {
        OffsetDateTime inicio = OffsetDateTime.of(fecha.getYear(),fecha.getMonthValue(),1,0,0,0,0,fecha.getOffset());
        OffsetDateTime fin = OffsetDateTime.from(inicio).plusMonths(1);
        Integer a = tiempoService.countTiempoTareaBetween(inicio,fin,usuario);
        Integer b = completadaService.countTiempoCompletadasBetween(inicio,fin,usuario);
        return new ReporteMensual(
                tareaService.countTareasBetween(inicio,fin,usuario)+completadaService.countCompletadasBetween(inicio,fin,usuario),
                (b == null ? 0 : b) + (a == null ? 0 : a)
        );
    }
}

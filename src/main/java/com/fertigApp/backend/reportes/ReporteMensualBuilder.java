package com.fertigApp.backend.reportes;

import com.fertigApp.backend.services.CompletadaService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.OffsetDateTime;

public class ReporteMensualBuilder implements ReporteBuilder{

    @Autowired
    CompletadaService completadaService;

    @Override
    public Reporte crearReporte(OffsetDateTime fecha) {
        return null;
    }
}

package com.fertigApp.backend.reportes;

import java.time.OffsetDateTime;

public interface ReporteBuilder {
    Reporte crearReporte(OffsetDateTime fecha);
}

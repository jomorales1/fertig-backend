package com.fertigApp.backend.reportes;

import com.fertigApp.backend.model.Usuario;

import java.time.OffsetDateTime;

public interface ReporteBuilder {
    Reporte crearReporte(OffsetDateTime fecha, Usuario usuario);
}

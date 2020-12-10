package com.fertigapp.backend.reportes;

import com.fertigapp.backend.model.Usuario;

import java.time.OffsetDateTime;

public interface ReporteBuilder {
    Reporte crearReporte(OffsetDateTime fecha, Usuario usuario);
}

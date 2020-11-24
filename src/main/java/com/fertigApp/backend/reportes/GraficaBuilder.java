package com.fertigApp.backend.reportes;

import com.fertigApp.backend.model.Usuario;

import java.time.OffsetDateTime;

public interface GraficaBuilder {
    Grafica crearGrafica(OffsetDateTime fecha, Usuario usuario);
}

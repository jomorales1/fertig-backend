package com.fertigapp.backend.reportes;

import com.fertigapp.backend.model.Usuario;

import java.time.OffsetDateTime;

public interface GraficaBuilder {
    Grafica crearGrafica(OffsetDateTime fecha, Usuario usuario);
}

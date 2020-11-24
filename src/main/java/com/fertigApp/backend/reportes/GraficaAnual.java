package com.fertigApp.backend.reportes;

import java.time.OffsetDateTime;
import java.util.List;

public class GraficaAnual extends Grafica{
    public GraficaAnual(List<OffsetDateTime> fechas, List<Integer> minutos, List<Integer> tareas) {
        super(fechas, minutos, tareas);
    }
}

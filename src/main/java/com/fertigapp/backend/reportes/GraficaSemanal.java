package com.fertigapp.backend.reportes;

import java.time.OffsetDateTime;
import java.util.List;

public class GraficaSemanal extends Grafica{
    public GraficaSemanal(List<OffsetDateTime> fechas, List<Integer> minutos, List<Integer> tareas) {
        super(fechas, minutos, tareas);
    }
}

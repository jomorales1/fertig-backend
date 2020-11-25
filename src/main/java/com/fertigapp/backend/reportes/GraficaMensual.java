package com.fertigapp.backend.reportes;

import java.time.OffsetDateTime;
import java.util.List;

public class GraficaMensual extends Grafica{
    public GraficaMensual(List<OffsetDateTime> fechas, List<Integer> minutos, List<Integer> tareas) {
        super(fechas, minutos, tareas);
    }
}

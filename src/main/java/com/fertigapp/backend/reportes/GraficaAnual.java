package com.fertigapp.backend.reportes;

import java.time.OffsetDateTime;
import java.util.List;

public class GraficaAnual extends Grafica{

    protected List<Integer> minutosMes;

    protected List<Integer> tareasMes;

    public GraficaAnual(List<OffsetDateTime> fechas, List<Integer> minutos, List<Integer> tareas, List<Integer> minutosMes, List<Integer> tareasMes) {
        super(fechas, minutos, tareas);
        this.minutosMes = minutosMes;
        this.tareasMes = tareasMes;
    }
}

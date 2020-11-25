package com.fertigapp.backend.reportes;

import java.time.OffsetDateTime;
import java.util.List;

public abstract class Grafica {
    List<OffsetDateTime> fechas;
    List<Integer> minutos;
    List<Integer> tareas;

    public Grafica(List<OffsetDateTime> fechas, List<Integer> minutos, List<Integer> tareas) {
        this.fechas = fechas;
        this.minutos = minutos;
        this.tareas = tareas;
    }

    public Grafica() {
    }

    public List<OffsetDateTime> getFechas() {
        return fechas;
    }

    public void setFechas(List<OffsetDateTime> fechas) {
        this.fechas = fechas;
    }

    public List<Integer> getMinutos() {
        return minutos;
    }

    public void setMinutos(List<Integer> minutos) {
        this.minutos = minutos;
    }

    public List<Integer> getTareas() {
        return tareas;
    }

    public void setTareas(List<Integer> tareas) {
        this.tareas = tareas;
    }
}

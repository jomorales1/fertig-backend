package com.fertigapp.backend.reportes;

public abstract class Reporte {

    protected Integer tareas;
    protected Integer minutos;

    public Reporte(Integer tareas, Integer minutos) {
        this.tareas = tareas;
        this.minutos = minutos;
    }

    public Reporte() {
    }

    public Integer getTareas() {
        return tareas;
    }

    public void setTareas(Integer tareas) {
        this.tareas = tareas;
    }

    public Integer getMinutos() {
        return minutos;
    }

    public void setMinutos(Integer minutos) {
        this.minutos = minutos;
    }
}

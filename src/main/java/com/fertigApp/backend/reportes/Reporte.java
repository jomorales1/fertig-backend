package com.fertigApp.backend.reportes;

public abstract class Reporte {

    protected Integer tareas;
    protected Integer horas;

    public Reporte(Integer tareas, Integer horas) {
        this.tareas = tareas;
        this.horas = horas;
    }
}

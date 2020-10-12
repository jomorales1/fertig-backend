package com.fertigApp.backend.requestModels;

import java.util.Date;

public class RequestCompletada {
    private Integer rutina;
    private Date fecha;

    public Integer getRutina() {
        return rutina;
    }

    public void setRutina(Integer rutina) {
        this.rutina = rutina;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

package com.fertigApp.backend.requestModels;

import java.util.Date;

public class RepeticionEvento {

    private int id;

    private Date fecha;

    public RepeticionEvento() {

    }

    public RepeticionEvento(int id, Date fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

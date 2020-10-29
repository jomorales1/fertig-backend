package com.fertigApp.backend.requestModels;

import java.util.Date;

public class RepeticionEvento {

    private int id;

    private String nombre;

    private Date fecha;

    private int duracion;

    public RepeticionEvento(int id, String nombre, Date fecha, int duracion) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.duracion = duracion;
    }

    public RepeticionEvento() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }
}

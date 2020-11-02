package com.fertigApp.backend.requestModels;


import java.time.LocalDateTime;

public class RequestCompletada {

    private Integer rutina;
    private LocalDateTime fecha;

    public RequestCompletada(){

    }

    public RequestCompletada(Integer rutina, LocalDateTime fecha) {
        this.rutina = rutina;
        this.fecha = fecha;
    }

    public Integer getRutina() {
        return rutina;
    }

    public void setRutina(Integer rutina) {
        this.rutina = rutina;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}

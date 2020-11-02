package com.fertigApp.backend.requestModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fertigApp.backend.model.Usuario;

import java.util.Date;

public class RequestTarea {

    private int id;

    private String nombre;

    private String descripcion;

    private Integer prioridad;

    private String etiqueta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer estimacion;

    private Date fechaInicio;

    private Date fechaFin;

    private boolean hecha;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer recordatorio;

    private Integer tiempoInvertido;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Integer getEstimacion() {
        return estimacion;
    }

    public void setEstimacion(Integer estimacion) {
        this.estimacion = estimacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean getHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }

    public Integer getTiempoInvertido() {
        return tiempoInvertido;
    }

    public void setTiempoInvertido(Integer tiempoInvertido) {
        this.tiempoInvertido = tiempoInvertido;
    }
}

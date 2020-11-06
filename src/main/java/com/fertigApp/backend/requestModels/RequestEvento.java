package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Usuario;

import java.time.LocalDateTime;

public class RequestEvento {

    private Usuario usuarioE;

    private String nombre;

    private String descripcion;

    private Integer prioridad;

    private String etiqueta;

    private Integer duracion;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private String recurrencia;

    private Integer recordatorio;

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

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
    }

    public Usuario getUsuarioE() {
        return usuarioE;
    }

    public void setUsuarioE(Usuario usuarioE) {
        this.usuarioE = usuarioE;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }
}

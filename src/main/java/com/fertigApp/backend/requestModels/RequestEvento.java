package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Usuario;

import java.io.Serializable;
import java.util.Date;

public class RequestEvento implements Serializable {

    private Usuario usuarioE;
    private String nombre;
    private String descripcion;
    private Integer prioridad;
    private String etiqueta;
    private Integer estimacion;
    private Date fechaInicio;
    private Date fechaFin;
    private String recurrencia;
    private Integer recordatorio;

    public Usuario getUsuarioE() {
        return usuarioE;
    }

    public void setUsuarioE(Usuario usuarioE) {
        this.usuarioE = usuarioE;
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

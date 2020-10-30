package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Completada;
import com.fertigApp.backend.model.Usuario;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

public class RequestRutina implements Serializable {

    private Usuario usuarioR;

    private String nombre;

    private String descripcion;

    private Integer prioridad;

    private String etiqueta;

    private Integer duracion;

    private Date fechaInicio;

    private Date fechaFin;

    private String recurrencia;

    private Integer recordatorio;

    private Time franjaInicio;

    private Time franjaFin;

    private List<Completada> completadas;

    public Usuario getUsuarioR() {
        return usuarioR;
    }

    public void setUsuarioR(Usuario usuarioR) {
        this.usuarioR = usuarioR;
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

    public Integer getDuracion() {
        return duracion;
    }

    public void setDuracion(Integer duracion) {
        this.duracion = duracion;
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

    public Time getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(Time franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public Time getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(Time franjaFin) {
        this.franjaFin = franjaFin;
    }

    public List<Completada> getCompletadas() {
        return completadas;
    }

    public void setCompletadas(List<Completada> completadas) {
        this.completadas = completadas;
    }
}

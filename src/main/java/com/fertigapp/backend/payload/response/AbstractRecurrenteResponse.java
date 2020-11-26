package com.fertigapp.backend.payload.response;

import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Rutina;
import com.fertigapp.backend.recurrentstrategy.EventoRecurrentEntityStrategy;
import com.fertigapp.backend.recurrentstrategy.RutinaRecurrentEntityStrategy;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.LinkedList;
import java.util.List;

//Clase principal de la cual heredan los eventos y las rutinas para obtener la fechas y los mensajes de repetición de
//los eventos y rutinas
public abstract class AbstractRecurrenteResponse implements Serializable {

    protected String recurrencia;
    protected OffsetTime franjaInicio;
    protected OffsetTime franjaFin;
    protected OffsetDateTime fechaInicio;
    protected OffsetDateTime fechaFin;
    protected int id;
    protected String nombre;
    protected String descripcion;
    protected Integer prioridad;
    protected String etiqueta;
    protected Integer duracion;
    protected String mensajeRecurrencia;

    protected AbstractRecurrenteResponse(){

    }

    protected AbstractRecurrenteResponse(Evento evento){
        this.id = evento.getId();
        this.nombre = evento.getNombre();
        this.descripcion = evento.getDescripcion();
        this.prioridad = evento.getPrioridad();
        this.etiqueta = evento.getEtiqueta();
        this.duracion = evento.getDuracion();
        this.fechaInicio = evento.getFechaInicio();
        this.fechaFin = evento.getFechaFin();
        this.recurrencia = evento.getRecurrencia();
        this.mensajeRecurrencia = new EventoRecurrentEntityStrategy(evento).getRecurrenceStrategy().getRecurrenceMessage();
    }

    protected AbstractRecurrenteResponse(Rutina rutina){
        this.id = rutina.getId();
        this.nombre = rutina.getNombre();
        this.descripcion = rutina.getDescripcion();
        this.prioridad = rutina.getPrioridad();
        this.etiqueta = rutina.getEtiqueta();
        this.duracion = rutina.getDuracion();
        this.fechaInicio = rutina.getFechaInicio();
        this.fechaFin = rutina.getFechaFin();
        this.franjaInicio = rutina.getFranjaInicio();
        this.franjaFin = rutina.getFranjaFin();
        this.recurrencia = rutina.getRecurrencia();
        this.mensajeRecurrencia = new RutinaRecurrentEntityStrategy(rutina).getRecurrenceStrategy().getRecurrenceMessage();
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

    public String getMensajeRecurrencia() {
        return mensajeRecurrencia;
    }

    public void setMensajeRecurrencia(String mensajeRecurrencia) {
        this.mensajeRecurrencia = mensajeRecurrencia;
    }

    public OffsetTime getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(OffsetTime franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public OffsetTime getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(OffsetTime franjaFin) {
        this.franjaFin = franjaFin;
    }

    public OffsetDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(OffsetDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public OffsetDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(OffsetDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }
}

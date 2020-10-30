package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Evento;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class BasicRequestEvento implements Serializable {

    protected String nombre;
    protected String descripcion;
    protected Integer prioridad;
    protected String etiqueta;
    protected Integer duracion;
    private Date fecha;

    public BasicRequestEvento(Evento evento) {
        this.nombre = evento.getNombre();
        this.descripcion = evento.getDescripcion();
        this.prioridad = evento.getPrioridad();
        this.etiqueta = evento.getEtiqueta();
        this.duracion = evento.getDuracion();


    }

    public List<Date> findFechas(Date fechaInicio, Date fechaFin, String recurrencia, boolean proximo){
        LinkedList<Date> fechas = new LinkedList<>();
        //1s;2s;l,x;1m;1a;2h;2s l,x;l-v;1m 15-20;
        switch (recurrencia.charAt(0)){
            case 'A':
              for(Date current = fechaInicio; current.before(fechaFin); current=new Date(current.getTime()+3600*24*365));
        }
        return fechas;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}

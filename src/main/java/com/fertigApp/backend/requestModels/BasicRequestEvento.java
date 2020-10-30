package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Evento;

import java.io.Serializable;
import java.util.Calendar;
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
        Character c = recurrencia.charAt(0);
        if(recurrencia.charAt(0) == 'E'){
            int punto = recurrencia.indexOf(".");
            int dias = Integer.parseInt(recurrencia.substring(1, punto));
            for(int i = 1; i<=7; i++) {
                if((dias & 1) == 1){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(fechaInicio);
                    calendar.set(Calendar.DAY_OF_WEEK,(i%7)+1);
                    if(calendar.getTime().before(fechaInicio)) calendar.add(Calendar.WEEK_OF_YEAR,1);
                    fechas.addAll(findFechas(calendar.getTime(),fechaFin,recurrencia.substring(punto+1),proximo));
                }
                dias = dias >> 1;
            }
        } else{
            int n = Integer.parseInt(recurrencia.substring(1));
            for(Date current = fechaInicio; current.before(fechaFin); current = add(current,n,c)) {
                fechas.add(current);
            }
        }

        return fechas;
    }

    private Date add(Date fecha, int n, Character t){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        switch (t) {
            case 'A' -> calendar.add(Calendar.YEAR, n);
            case 'M' -> calendar.add(Calendar.MONTH, n);
            case 'S' -> calendar.add(Calendar.WEEK_OF_YEAR, n);
            case 'D' -> calendar.add(Calendar.DAY_OF_YEAR, n);
            case 'H' -> calendar.add(Calendar.HOUR, n);
        }
        return calendar.getTime();
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

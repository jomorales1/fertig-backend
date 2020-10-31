package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Rutina;

import java.io.Serializable;
import java.util.*;

//Clase principal de la cual heredan los eventos y las rutinas para obtener la fechas y los mensajes de repetición de
//los eventos y rutinas
public abstract class Recurrente implements Serializable {

    protected int id;
    protected String nombre;
    protected String descripcion;
    protected Integer prioridad;
    protected String etiqueta;
    protected Integer duracion;
    protected String mensajeRecurrencia;

    public Recurrente(){

    }

    public Recurrente(Evento evento){
        this.id = evento.getId();
        this.nombre = evento.getNombre();
        this.descripcion = evento.getDescripcion();
        this.prioridad = evento.getPrioridad();
        this.etiqueta = evento.getEtiqueta();
        this.duracion = evento.getDuracion();
        this.mensajeRecurrencia = getMensajeRecurrencia(evento.getRecurrencia());
    }

    public Recurrente(Rutina rutina){
        this.id = rutina.getId();
        this.nombre = rutina.getNombre();
        this.descripcion = rutina.getDescripcion();
        this.prioridad = rutina.getPrioridad();
        this.etiqueta = rutina.getEtiqueta();
        this.duracion = rutina.getDuracion();
        this.mensajeRecurrencia = getMensajeRecurrencia(rutina.getRecurrencia());
    }

    public static List<Date> findFechas(Date fechaInicio, Date fechaFin, String recurrencia, boolean proximo){
        LinkedList<Date> fechas = new LinkedList<>();
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

    protected static Date add(Date fecha, int n, Character t){
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

    public static String getMensajeRecurrencia(String recurrencia) {
        String mensajeRecurrencia;
        if(recurrencia.charAt(0) == 'E'){
            int point = recurrencia.indexOf(".");
            int codDias = Integer.parseInt(recurrencia.substring(1,point));
            LinkedList<String> dias = new LinkedList<>();
            for(int i = 1; i<=7; i++) {
                if((codDias & 1) == 1){
                    switch (i){
                        case 1 -> dias.add("Lunes");
                        case 2 -> dias.add("Martes");
                        case 3 -> dias.add("Miercoles");
                        case 4 -> dias.add("Jueves");
                        case 5 -> dias.add("Viernes");
                        case 6 -> dias.add("Sabado");
                        case 7 -> dias.add("Domingo");
                    }
                }
                codDias = codDias >> 1;
            }
            mensajeRecurrencia = "Todos los ";
            for(String dia : dias){
                if(dia.equals(dias.getLast())){
                    mensajeRecurrencia = mensajeRecurrencia.substring(0, mensajeRecurrencia.length()-2) + " y ";
                }
                mensajeRecurrencia += dia + ", ";
            }
            mensajeRecurrencia += getMensajeRecurrencia(recurrencia.substring(point+1)).toLowerCase();
        } else {
            mensajeRecurrencia = "Cada ";
            int n = Integer.parseInt(recurrencia.substring(1));
            if(n == 1) {
                switch (recurrencia.charAt(0)) {
                    case 'A' -> mensajeRecurrencia += "año";
                    case 'M' -> mensajeRecurrencia += "mes";
                    case 'S' -> mensajeRecurrencia += "semana";
                    case 'D' -> mensajeRecurrencia += "dia";
                    case 'H' -> mensajeRecurrencia += "hora";
                }
            } else {
                mensajeRecurrencia += recurrencia.substring(1) + " ";
                switch (recurrencia.charAt(0)) {
                    case 'A' -> mensajeRecurrencia += "años";
                    case 'M' -> mensajeRecurrencia += "meses";
                    case 'S' -> mensajeRecurrencia += "semanas";
                    case 'D' -> mensajeRecurrencia += "dias";
                    case 'H' -> mensajeRecurrencia += "horas";
                }
            }
        }
        return mensajeRecurrencia;
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
}

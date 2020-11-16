package com.fertigApp.backend.payload.response;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Rutina;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

//Clase principal de la cual heredan los eventos y las rutinas para obtener la fechas y los mensajes de repetición de
//los eventos y rutinas
public abstract class AbstractRecurrenteResponse implements Serializable {

    protected String recurrencia;
    protected LocalTime franjaInicio;
    protected LocalTime franjaFin;
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
        this.mensajeRecurrencia = getMensajeRecurrencia(evento.getRecurrencia());
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
        this.mensajeRecurrencia = getMensajeRecurrencia(rutina.getRecurrencia());

    }

    public static List<OffsetDateTime> findFechas(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia){
        LinkedList<OffsetDateTime> fechas = new LinkedList<>();
        Character c = recurrencia.charAt(0);
        if(recurrencia == null) {
            fechas.add(OffsetDateTime.from(fechaFin));
        } else if(recurrencia.charAt(0) == 'E'){
            int punto = recurrencia.indexOf(".");
            int dias = Integer.parseInt(recurrencia.substring(1, punto));
            for(long i = 1; i<8;  i++) {
                if((dias & 1) == 1){
                    OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
                    fechaI = fechaI.plusDays(i - fechaI.getDayOfWeek().getValue());
                    fechas.addAll(findFechas(fechaI,fechaFin,recurrencia.substring(punto+1)));
                }
                dias = dias >> 1;
            }
        } else{
            int n = Integer.parseInt(recurrencia.substring(1));
            for(OffsetDateTime current = OffsetDateTime.from(fechaInicio); current.isBefore(fechaFin); current = add(current,n,c)) {
                fechas.add(current);
            }
        }
        return fechas;
    }

    public static List<OffsetDateTime> findFechas(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia, int duracion, LocalTime franjaInicio, LocalTime franjaFin){
        LinkedList<OffsetDateTime> fechas = new LinkedList<>();
        if(recurrencia == null) {
            fechas.add(OffsetDateTime.from(fechaFin));
        } else if(recurrencia.charAt(0) == 'H'){
            OffsetDateTime franjaI = fechaInicio.toLocalDate().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
            OffsetDateTime franjaF = fechaInicio.toLocalDate().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            if(!franjaF.isAfter(franjaI)) franjaF = franjaF.plusDays(1);
            int d = Integer.parseInt(recurrencia.substring(1));
            while(fechaI.isBefore(fechaFin)) {
                if (fechaI.isAfter(franjaI) && fechaI.isBefore(franjaF)) fechas.add(fechaI);
                fechaI = fechaI.plusHours(d);
                if(fechaI.isAfter(franjaF)){
                    franjaI = franjaI.plusDays(1);
                    franjaF = franjaF.plusDays(1);
                }
            }
        } else{
            return findFechas(fechaInicio, fechaFin, recurrencia);
        }
        return fechas;
    }

    protected static OffsetDateTime add(OffsetDateTime fecha, int n, Character t){
        OffsetDateTime fechaFinal;
        switch (t) {
            case 'A' : fechaFinal = fecha.plusYears(n); break;
            case 'M' : fechaFinal = fecha.plusMonths(n); break;
            case 'S' : fechaFinal = fecha.plusWeeks(n); break;
            case 'D' : fechaFinal = fecha.plusDays(n); break;
            default : fechaFinal = fecha.plusHours(n); break;
        }
        return fechaFinal;
    }

    public static OffsetDateTime findSiguiente(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia) {
        OffsetDateTime fecha = OffsetDateTime.now();
        if(recurrencia == null) {
            return OffsetDateTime.from(fechaFin);
        } else if(recurrencia.charAt(0) == 'E'){
            int punto = recurrencia.indexOf(".") ;
            int d = Integer.parseInt(recurrencia.substring(1, punto));
            int dia = fecha.getDayOfWeek().getValue() - 1;
            while(fecha.compareTo(fechaFin)<1 && ((d >> dia) & 1) != 1) {
                dia = (dia+1)%7;
                fecha = fecha.plusDays(1);
            }
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            fechaI = fechaI.plusDays((long) fecha.getDayOfWeek().getValue()-fechaI.getDayOfWeek().getValue());
            while (fechaI.isBefore(fecha) || fechaI.isBefore(fechaInicio)) fechaI = fechaI.plusWeeks(Integer.parseInt(recurrencia.substring(punto+2)));
            if (fechaI.isAfter(fecha)) return fechaI;
            return findSiguiente(fechaInicio, fechaFin, 'E'+Integer.toString(d&(127-(int)Math.pow(2, fechaI.getDayOfWeek().getValue())))+recurrencia.substring(punto));
        } else {
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            int d = Integer.parseInt(recurrencia.substring(1));
            while(fechaI.isBefore(fecha)){
                fechaI = add(fechaI,d,recurrencia.charAt(0));
            }
            return fechaI;
        }
    }

    public static OffsetDateTime findSiguiente(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia, int duracion, LocalTime franjaInicio, LocalTime franjaFin) {
        if(recurrencia == null) {
            return OffsetDateTime.from(fechaFin);
        } else if(recurrencia.charAt(0) == 'H'){
            OffsetDateTime fecha = OffsetDateTime.now();
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            OffsetDateTime franjaI;
            OffsetDateTime franjaF;
            if(fecha.compareTo(fechaInicio) < 0){
                franjaI = fechaInicio.toLocalDate().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
                franjaF = fechaInicio.toLocalDate().atTime(franjaFin).atOffset(fechaInicio.getOffset());
            } else {
                franjaI = LocalDate.now().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
                franjaF = LocalDate.now().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
            }
            if(!franjaF.isAfter(franjaI)) franjaF = franjaF.plusDays(1);
            int d = Integer.parseInt(recurrencia.substring(1));
            while(fechaI.isBefore(fecha) || fechaI.isBefore(franjaI) || fechaI.isAfter(franjaF)){
                fechaI = fechaI.plusHours(d);
                if(fechaI.isAfter(franjaF)){
                    franjaF = franjaF.plusDays(1);
                    franjaI = franjaI.plusDays(1);
                }
            }
            return fechaI;
        } else {
            return findSiguiente(fechaInicio, fechaFin, recurrencia);
        }
    }

    public static OffsetDateTime findAnterior(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia) {
        OffsetDateTime fecha = OffsetDateTime.now();
        if(recurrencia == null) {
            return OffsetDateTime.from(fechaFin);
        } else if(recurrencia.charAt(0) == 'E'){
            int punto = recurrencia.indexOf(".") ;
            int d = Integer.parseInt(recurrencia.substring(1, punto));
            int dia = fecha.getDayOfWeek().getValue() - 1;
            while(fecha.compareTo(fechaInicio)>0) {
                if (((d >> dia) & 1) == 1) {
                    break;
                }
                dia = (dia+6)%7;
                fecha = fecha.minusDays(1);
            }
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            fechaI = fechaI.plusDays((long) fecha.getDayOfWeek().getValue()-fechaI.getDayOfWeek().getValue());
            if(fechaI.isBefore(fechaInicio)) fechaI = fechaI.plusWeeks(1);
            while (fechaI.isBefore(fecha)) fechaI = fechaI.plusWeeks(Integer.parseInt(recurrencia.substring(punto+2)));
            fecha = OffsetDateTime.now();
            if(!fechaI.isBefore(fecha)) fechaI = fechaI.minusWeeks(Integer.parseInt(recurrencia.substring(punto+2)));
            if (fechaI.isBefore(fecha) && fechaI.isAfter(fechaInicio)) return fechaI;
            else if(fecha.isAfter(fechaInicio)) return fechaInicio;
            return null;
        } else {
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);

            int d = Integer.parseInt(recurrencia.substring(1));
            while(fechaI.isBefore(fecha)){
                fechaI = add(fechaI,d,recurrencia.charAt(0));
            }
            fechaI = add(fechaI,-d,recurrencia.charAt(0));
            if (fechaI.isBefore(fecha) && fechaI.isAfter(fechaInicio)) return fechaI;
            else if(fecha.isAfter(fechaInicio)) return fechaInicio;
            else return null;
        }
    }

    public static OffsetDateTime findAnterior(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, String recurrencia, int duracion, LocalTime franjaInicio, LocalTime franjaFin) {
        if(recurrencia == null) {
            return OffsetDateTime.from(fechaFin);
        } else if(recurrencia.charAt(0) == 'H'){
            OffsetDateTime fecha = OffsetDateTime.now();
            OffsetDateTime fechaI = OffsetDateTime.from(fechaInicio);
            OffsetDateTime franjaI = LocalDate.now().atTime(franjaInicio).atOffset(fechaInicio.getOffset());
            OffsetDateTime franjaF = LocalDate.now().atTime(franjaFin).atOffset(fechaInicio.getOffset());
            if(!franjaF.isAfter(franjaI)) franjaF = franjaF.plusDays(1);
            int d = Integer.parseInt(recurrencia.substring(1));
            while(fechaI.isBefore(fecha) || fechaI.isBefore(franjaI) || fechaI.isAfter(franjaF)){
                fechaI = fechaI.plusHours(d);
                if(fechaI.isAfter(franjaF)){
                    franjaF = franjaF.plusDays(1);
                    franjaI = franjaI.plusDays(1);
                }
            }
            while(fechaI.isAfter(fecha) || fechaI.isBefore(franjaI) || fechaI.isAfter(franjaF)){
                fechaI = fechaI.minusHours(d);
                if(fechaI.isBefore(franjaI)){
                    franjaF = franjaF.minusDays(1);
                    franjaI = franjaI.minusDays(1);
                }
            }
            if (fechaI.isBefore(fecha) && fechaI.isAfter(fechaInicio)) return fechaI;
                //mejorar condicion
            else if(fecha.isAfter(fechaInicio)) return fechaInicio;
            return null;
        } else {
            return findAnterior(fechaInicio, fechaFin, recurrencia);
        }
    }

    public static String getMensajeRecurrencia(String recurrencia) {
        String mensajeRecurrencia;
        if(recurrencia == null){
            mensajeRecurrencia = "Sin repetición";
        } else if(recurrencia.charAt(0) == 'E'){
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
                        case 6 -> dias.add("Sabados");
                        default -> dias.add("Domingos");
                    }
                }
                codDias = codDias >> 1;
            }
            mensajeRecurrencia = "Todos los ";
            for(String dia : dias){
                if(dia.equals(dias.getLast())&&dias.size()>1){
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
                    default -> mensajeRecurrencia += "hora";
                }
            } else {
                mensajeRecurrencia += recurrencia.substring(1) + " ";
                switch (recurrencia.charAt(0)) {
                    case 'A' -> mensajeRecurrencia += "años";
                    case 'M' -> mensajeRecurrencia += "meses";
                    case 'S' -> mensajeRecurrencia += "semanas";
                    case 'D' -> mensajeRecurrencia += "dias";
                    default -> mensajeRecurrencia += "horas";
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

    public LocalTime getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(LocalTime franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public LocalTime getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(LocalTime franjaFin) {
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

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

        private int nivel;

        private boolean hecha;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Integer recordatorio ;

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

        public int getPrioridad() {
            return prioridad;
        }

        public void setPrioridad(int prioridad) {
            this.prioridad = prioridad;
        }

        public String getEtiqueta() {
            return etiqueta;
        }

        public void setEtiqueta(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        public int getEstimacion() {
            return estimacion;
        }

        public void setEstimacion(int estimacion) {
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

        public int getNivel() {
            return nivel;
        }

        public void setNivel(int nivel) {
            this.nivel = nivel;
        }

        public boolean getHecha() {
            return hecha;
        }

        public void setHecha(boolean hecha) {
            this.hecha = hecha;
        }

        public int getRecordatorio() {
            return recordatorio;
        }

        public void setRecordatorio(int recordatorio) {
            this.recordatorio = recordatorio;
        }
}

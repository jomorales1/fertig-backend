package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="tarea", schema="mydb")
public class Tarea implements Serializable {

    @Id
    @SequenceGenerator(name = "id_tarea_generator",
        sequenceName = "public.tarea_tarea_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_tarea_generator")
    @Column(name="id_tarea")
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuarioT;

    private String nombre;

    private String descripcion;

    private Integer prioridad;

    private String etiqueta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer estimacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_fin")
    private Date fechaFin;

    private int nivel;

    private int hecha;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer recordatorio ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuarioT() {
        return usuarioT;
    }

    public void setUsuario(Usuario usuarioT) {
        this.usuarioT = usuarioT;
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

    public int getHecha() {
        return hecha;
    }

    public void setHecha(int hecha) {
        this.hecha = hecha;
    }

    public int getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(int recordatorio) {
        this.recordatorio = recordatorio;
    }
}

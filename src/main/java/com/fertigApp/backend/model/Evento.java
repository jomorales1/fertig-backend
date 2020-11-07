package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name="evento")
public class Evento implements Serializable {

    @Id
    @SequenceGenerator(name = "id_evento_generator",
        sequenceName = "public.evento_evento_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_evento_generator")
    @Column(name="id_evento")
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario")
    protected Usuario usuarioE;

    protected String nombre;

    protected String descripcion;

    protected Integer prioridad;

    protected String etiqueta;

    protected Integer duracion;

    @Column(name="fecha_inicio",columnDefinition="DATETIME")
    protected OffsetDateTime fechaInicio;

    @Column(name="fecha_fin",columnDefinition="DATETIME")
    protected OffsetDateTime fechaFin;

    protected String recurrencia;

    protected Integer recordatorio;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuarioE;
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioE = usuario;
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

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }
}

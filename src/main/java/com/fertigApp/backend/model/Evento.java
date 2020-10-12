package com.fertigApp.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="evento", schema="mydb")
public class Evento implements Serializable {

    @Id
    @SequenceGenerator(name = "id_evento_generator",
        sequenceName = "public.evento_evento_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_evento_generator")
    @Column(name="id_evento")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuarioE;

    private String nombre;

    private String descripcion;

    private Integer prioridad;

    private String etiqueta;

    private Integer estimacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_fin")
    private Date fechaFin;

    private String recurrencia;

    private Integer recordatorio;

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

    public String getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }

    public int getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(int recordatorio) {
        this.recordatorio = recordatorio;
    }
}

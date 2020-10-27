package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="rutina")
public class Rutina implements Serializable {

    @Id
    @SequenceGenerator(name = "id_rutina_generator",
        sequenceName = "public.rutina_rutina_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_rutina_generator")
    @Column(name="id_rutina")
    private int id;

    @ManyToOne
    @JoinColumn(name = "usuario")
    private Usuario usuarioR;

    private String nombre;

    private String descripcion;

    private int prioridad;

    private String etiqueta;

    private int duracion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_inicio")
    private Date fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="fecha_fin")
    private Date fechaFin;

    private String recurrencia;

    private int recordatorio;

    private Time franjaInicio;

    private Time franjaFin;

    @JsonIgnore
    @OneToMany(mappedBy = "rutinaC")
    private List<Completada> completadas;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuarioR;
    }

    public void setUsuario(Usuario usuario) {
        this.usuarioR = usuario;
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

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
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

    public int getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(int recordatorio) {
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

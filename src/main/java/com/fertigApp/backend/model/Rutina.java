package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="rutina")
public class Rutina implements Serializable {

    @Id
    @SequenceGenerator(name = "id_rutina_generator",
        sequenceName = "public.rutina_rutina_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "id_rutina_generator")
    @Column(name="id_rutina")
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario")
    protected Usuario usuarioR;

    protected String nombre;

    protected String descripcion;

    protected int prioridad;

    protected String etiqueta;

    protected int duracion;

    @Column(name="fecha_inicio",columnDefinition="DATETIME")
    protected OffsetDateTime fechaInicio;

    @Column(name="fecha_fin",columnDefinition="DATETIME")
    protected OffsetDateTime fechaFin;

    protected String recurrencia;

    protected int recordatorio;

    @Column(name="franja_inicio",columnDefinition = "TIME")
    protected LocalTime franjaInicio;

    @Column(name="franja_fin",columnDefinition = "TIME")
    protected LocalTime franjaFin;

    @JsonIgnore
    @OneToMany(mappedBy = "rutinaC")
    private List<Completada> completadas;

    @OneToMany(mappedBy = "rutinaT", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Tarea> subtareas;

    public void addSubtarea(Tarea subtarea) {
        if (this.subtareas == null)
            this.subtareas = new HashSet<>();
        this.subtareas.add(subtarea);
    }

    public boolean deleteSubtarea(Tarea subtarea) {
        return this.subtareas.removeIf(tarea -> tarea.getId() == subtarea.getId());
    }

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

    public int getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(int recordatorio) {
        this.recordatorio = recordatorio;
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

    public List<Completada> getCompletadas() {
        return completadas;
    }

    public void setCompletadas(List<Completada> completadas) {
        this.completadas = completadas;
    }

    public Usuario getUsuarioR() {
        return usuarioR;
    }

    public void setUsuarioR(Usuario usuarioR) {
        this.usuarioR = usuarioR;
    }

    public Set<Tarea> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(Set<Tarea> subtareas) {
        this.subtareas = subtareas;
    }

}

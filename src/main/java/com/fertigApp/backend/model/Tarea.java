package com.fertigApp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name="tarea")
public class Tarea implements Serializable {

    @Id
    @SequenceGenerator(name = "id_tarea_generator",
        sequenceName = "public.tarea_tarea_id_seq", allocationSize = 1)
    @GeneratedValue (strategy = GenerationType.IDENTITY, generator = "id_tarea_generator")
    @Column(name="id_tarea")
    private int id;

    @JsonIgnore
    @OneToMany(mappedBy = "tarea")
    private Set<TareaDeUsuario> usuariosT;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_padre")
    private Tarea padre;

    @OneToMany(mappedBy = "padre", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Tarea> subtareas;

    protected String nombre;

    protected String descripcion;

    protected Integer prioridad;

    protected String etiqueta;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected Integer estimacion;

    @Column(name="fecha_fin", columnDefinition="DATETIME")
    protected OffsetDateTime fechaFin;

    private int nivel;

    protected boolean hecha;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer recordatorio;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_rutina")
    private Rutina rutinaT;

    public void addSubtarea(Tarea tarea) {
        if (this.subtareas == null) {
            this.subtareas = new HashSet<>();
        }
        this.subtareas.add(tarea);
    }

    public boolean deleteSubtarea(Tarea subtarea) {
        return this.subtareas.removeIf(tarea -> tarea.getId() == subtarea.getId());
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public Integer getEstimacion() {
        return estimacion;
    }

    public OffsetDateTime getFechaFin() {
        return fechaFin;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean getHecha() {
        return hecha;
    }

    public void setHecha(boolean hecha) {
        this.hecha = hecha;
    }

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }

    public Set<TareaDeUsuario> getUsuariosT() {
        return usuariosT;
    }

    public Tarea getPadre() {
        return padre;
    }

    public Set<Tarea> getSubtareas() {
        return subtareas;
    }

    public boolean isHecha() {
        return hecha;
    }

    public Rutina getRutinaT() {
        return rutinaT;
    }

    public void setRutinaT(Rutina rutinaT) {
        this.rutinaT = rutinaT;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public void setEstimacion(Integer estimacion) {
        this.estimacion = estimacion;
    }

    public void setFechaFin(OffsetDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public void setUsuariosT(Set<TareaDeUsuario> usuariosT) {
        this.usuariosT = usuariosT;
    }

    public void setPadre(Tarea padre) {
        this.padre = padre;
    }

    public void setSubtareas(Set<Tarea> subtareas) {
        this.subtareas = subtareas;
    }

}

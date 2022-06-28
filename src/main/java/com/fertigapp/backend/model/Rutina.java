package com.fertigapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fertigapp.backend.auth.jwt.AuthEntryPointJwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="rutina")
public class Rutina implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

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

    protected Integer recordatorio;

    @Column(name="franja_inicio",columnDefinition = "TIME")
    protected OffsetTime franjaInicio;

    @Column(name="franja_fin",columnDefinition = "TIME")
    protected OffsetTime franjaFin;

    @PrePersist
    public void onPrePersist() {
        logger.info("Rutina creada");
    }

    @PreUpdate
    public void onPreUpdate() {
        logger.info("Rutina actualizada");
    }

    @PreRemove
    public void onPreRemove() {
        logger.info("Rutina eliminada");
    }

    @JsonIgnore
    @OneToMany(mappedBy = "rutinaC")
    private Set<Completada> completadas;

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

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }

    public String getRecurrencia() {
        return recurrencia;
    }

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }

    public OffsetTime getFranjaInicio() {
        return franjaInicio;
    }

    public void setFranjaInicio(OffsetTime franjaInicio) {
        this.franjaInicio = franjaInicio;
    }

    public OffsetTime getFranjaFin() {
        return franjaFin;
    }

    public void setFranjaFin(OffsetTime franjaFin) {
        this.franjaFin = franjaFin;
    }

    public Set<Completada> getCompletadas() {
        return completadas;
    }

    public void setCompletadas(Set<Completada> completadas) {
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

    public void setFechaInicio(OffsetDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(OffsetDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public OffsetDateTime getFechaFin() {
        return fechaFin;
    }

    public OffsetDateTime getFechaInicio() {
        return fechaInicio;
    }

}

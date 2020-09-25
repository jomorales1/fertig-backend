package com.fertigapp.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="usuario", schema="mydb")
public class Usuario implements Serializable {
    @Id
    private String correo;

    private String nombre;

    private String password;
/*
    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Tarea> tareas;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Rutina> rutinas;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario")
    private List<Evento> eventos;
*/
    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

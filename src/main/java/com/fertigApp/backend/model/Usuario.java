package com.fertigApp.backend.model;


import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity// This tells Hibernate to make a table out of this class
@Table(name = "usuario", schema = "mydb")
public class Usuario implements Serializable {
	@Id
	private String correo;

	private String nombre;

	@JsonIgnore
	private String password;


	@JsonIgnore
	@OneToMany(mappedBy = "usuarioT")
	private List<Tarea> tareas;


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

	public List<Tarea> getTareas() {
		return tareas;
	}

	public void setTareas(List<Tarea> tareas) {
		this.tareas = tareas;
	}
}

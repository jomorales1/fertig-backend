package com.fertigApp.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

@Entity// This tells Hibernate to make a table out of this class
@Table(name = "usuario", schema = "mydb")
public class Usuario implements Serializable {

	@Id
	private String usuario;

	private String correo;

	private String nombre;

	private boolean google;

	private boolean facebook;

	@JsonIgnore
	private String password;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioT")
	private List<Tarea> tareas;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioE")
	private List<Evento> eventos;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioR")
	private List<Rutina> rutinas;

	public Usuario() { }

	public Usuario(String usuario, String correo, String password, String nombre) {
		this.usuario = usuario;
		this.correo = correo;
		this.nombre = nombre;
		this.password = password;
	}

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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public List<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(List<Evento> eventos) {
		this.eventos = eventos;
	}

	public List<Rutina> getRutinas() {
		return rutinas;
	}

	public void setRutinas(List<Rutina> rutinas) {
		this.rutinas = rutinas;
	}

	public boolean isGoogle() {
		return google;
	}

	public void setGoogle(boolean google) {
		this.google = google;
	}

	public boolean isFacebook() {
		return facebook;
	}

	public void setFacebook(boolean facebook) {
		this.facebook = facebook;
	}
}

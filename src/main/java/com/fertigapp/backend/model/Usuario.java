package com.fertigapp.backend.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity// This tells Hibernate to make a table out of this class
@Table(name = "usuario")
public class Usuario implements Serializable {

	@Id
	private String usuario;

	protected String correo;

	protected String nombre;

	private boolean google;

	private boolean facebook;

	@JsonIgnore
	private String password;

	@JsonIgnore
	@OneToMany(mappedBy = "usuario")
	private Set<TareaDeUsuario> tareas;

	@JsonIgnore
	@ManyToMany(cascade = {CascadeType.ALL})
	@JoinTable(
			name = "preferido",
			joinColumns = {@JoinColumn(name="usuario")},
			inverseJoinColumns = {@JoinColumn(name="id_sonido")}
	)
	private Set<Sonido> sonidos;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioE")
	private Set<Evento> eventos;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioR")
	private Set<Rutina> rutinas;

    @JsonIgnore
    @ManyToMany(mappedBy = "agregados", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Usuario> agregadores;

    @JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	@JoinTable(
			name = "amigo",
			joinColumns = {@JoinColumn(name = "agregador")},
			inverseJoinColumns = {@JoinColumn(name = "agregado")}
	)
	private Set<Usuario> agregados;

    @JsonIgnore
	@OneToMany(mappedBy = "usuarioF")
    private Set<FirebaseNotificationToken> notificationTokens;

	@JsonIgnore
	@OneToMany(mappedBy = "usuarioFL")
	private Set<FranjaActiva> franjasActivas;

	public Usuario() { }

	public Usuario(String usuario, String correo, String password, String nombre) {
		this.usuario = usuario;
		this.correo = correo;
		this.nombre = nombre;
		this.password = password;
	}

	public void addAmigo(Usuario amigo) {
		if (this.agregados == null) {
			this.agregados = new HashSet<>();
		}
		amigo.addAgregador(this);
		this.agregados.add(amigo);
	}

	public void addAgregador(Usuario agregador) {
		if (this.agregadores == null) {
			this.agregadores = new HashSet<>();
		}
		this.agregadores.add(agregador);
	}

	public boolean deleteAgregado(Usuario agregado) {
		boolean result = this.agregados.removeIf(amigo -> amigo.getUsuario().equals(agregado.getUsuario()));
		agregado.deleteAgregador(this);
		return result;
	}

	public void deleteAgregador(Usuario agregador) {
		this.agregadores.removeIf(amigo -> amigo.getUsuario().equals(agregador.getUsuario()));
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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Set<Evento> getEventos() {
		return eventos;
	}

	public void setEventos(Set<Evento> eventos) {
		this.eventos = eventos;
	}

	public Set<Rutina> getRutinas() {
		return rutinas;
	}

	public void setRutinas(Set<Rutina> rutinas) {
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

	public Set<TareaDeUsuario> getTareas() {
		return tareas;
	}

	public void setTareas(Set<TareaDeUsuario> tareas) {
		this.tareas = tareas;
	}

	public Set<Sonido> getSonidos() {
		return sonidos;
	}

	public void setSonidos(Set<Sonido> sonidos) {
		this.sonidos = sonidos;
	}

	public Set<Usuario> getAgregadores() {
		return agregadores;
	}

	public void setAgregadores(Set<Usuario> amigos) {
		this.agregadores = amigos;
	}

	public Set<Usuario> getAgregados() {
		return agregados;
	}

	public void setAgregados(Set<Usuario> agregados) {
		this.agregados = agregados;
	}

	public Set<FranjaActiva> getFranjasLibres() {
		return franjasActivas;
	}

	public void setFranjasLibres(Set<FranjaActiva> franjasActivas) {
		this.franjasActivas = franjasActivas;
	}
}

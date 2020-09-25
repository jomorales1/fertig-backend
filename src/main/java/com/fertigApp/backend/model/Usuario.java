package com.fertigApp.backend.model;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity// This tells Hibernate to make a table out of this class
@Table(name = "usuario", schema = "mydb")
public class Usuario implements Serializable {
	@Id
	private String correo;

	private String nombre;

	private String password;
	/*
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "amigo",
			joinColumns = { @JoinColumn(name = "correo") } ,
			inverseJoinColumns = { @JoinColumn(name = "correo") }
	)
	private List<Usuario> agregados;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "amigo",
			joinColumns = { @JoinColumn(name = "correo") },
			inverseJoinColumns = { @JoinColumn(name = "correo") }
	)
	private List<Usuario> agregadores;*/


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

package com.fertigApp.backend.requestModels;


import java.io.Serializable;

public class RequestUsuario implements Serializable {
	private String correo;

	private String nombre;

	private String usuario;

	private String password;

	public RequestUsuario() { };

	public RequestUsuario(String correo, String nombre, String usuario, String password) {
		this.correo = correo;
		this.nombre = nombre;
		this.usuario = usuario;
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

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}

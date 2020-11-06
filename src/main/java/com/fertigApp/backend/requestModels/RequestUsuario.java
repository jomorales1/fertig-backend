package com.fertigApp.backend.requestModels;


import com.fertigApp.backend.model.Usuario;

import java.io.Serializable;

public class RequestUsuario extends Usuario implements Serializable {

//	private String usuario;
//	private String password;
//	private String correo;
//	private String nombre;
//
//	public RequestUsuario(String usuario, String correo, String password, String nombre) {
//		this.usuario = usuario;
//		this.correo = correo;
//		this.password = password;
//		this.nombre = nombre;
//	}
//
//	public RequestUsuario() { }
//
//	public String getUsuario() {
//		return usuario;
//	}
//
//	public void setUsuario(String usuario) {
//		this.usuario = usuario;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getCorreo() {
//		return correo;
//	}
//
//	public void setCorreo(String correo) {
//		this.correo = correo;
//	}
//
//	public String getNombre() {
//		return nombre;
//	}
//
//	public void setNombre(String nombre) {
//		this.nombre = nombre;
//	}

//	private String correo;
//
//	private String nombre;

	private String usuario;

	private String password;

	public RequestUsuario(String correo, String nombre, String usuario, String password) {
		this.correo = correo;
		this.nombre = nombre;
		this.usuario = usuario;
		this.password = password;
	}

    public RequestUsuario() {

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

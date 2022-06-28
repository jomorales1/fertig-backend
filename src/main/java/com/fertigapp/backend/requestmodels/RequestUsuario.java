package com.fertigapp.backend.requestmodels;


import com.fertigapp.backend.model.Usuario;

import java.io.Serializable;

public class RequestUsuario extends Usuario implements Serializable {
	private String usuario;
	private String recaptcha;
	private String password;

	public RequestUsuario(String correo, String nombre, String usuario, String password) {
		this.correo = correo;
		this.nombre = nombre;
		this.usuario = usuario;
		this.password = password;
		this.recaptcha = " ";
	}

	public RequestUsuario(String correo, String nombre, String usuario, String password, String recaptcha) {
		this.correo = correo;
		this.nombre = nombre;
		this.usuario = usuario;
		this.password = password;
		this.recaptcha = recaptcha;
	}

    public RequestUsuario() {

    }

    @Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String getUsuario() {
		return usuario;
	}

	@Override
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getRecaptcha() {
		return recaptcha;
	}

	public void setRecaptcha(String reCaptcha) {
		this.recaptcha = reCaptcha;
	}
}

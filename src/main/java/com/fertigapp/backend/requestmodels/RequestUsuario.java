package com.fertigapp.backend.requestmodels;


import com.fertigapp.backend.model.Usuario;

import java.io.Serializable;

public class RequestUsuario extends Usuario implements Serializable {
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
}

package com.fertigapp.backend.requestmodels;

import com.fertigapp.backend.model.Evento;
import com.fertigapp.backend.model.Usuario;

public class RequestEvento extends Evento {

    public Usuario getUsuarioE(){
        return super.getUsuario();
    }

    public void setUsuarioE(Usuario usuario){
        super.setUsuario(usuario);
    }

}

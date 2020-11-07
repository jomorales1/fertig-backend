package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.model.Evento;
import com.fertigApp.backend.model.Usuario;

public class RequestEvento extends Evento {

    public Usuario getUsuarioE(){
        return super.getUsuario();
    }

    public void setUsuarioE(Usuario usuario){
        super.setUsuario(usuario);
    }

}

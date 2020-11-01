package com.fertigApp.backend.requestModels;

import com.fertigApp.backend.payload.response.AbstractRecurrenteResponse;
import com.fertigApp.backend.model.Usuario;

import java.util.Date;

public class RequestEvento extends AbstractRecurrenteResponse {

    private Usuario usuarioE;
    private Date fechaInicio;
    private Date fechaFin;
    private String recurrencia;
    private Integer recordatorio;

    public Usuario getUsuarioE() {
        return usuarioE;
    }

    public void setUsuarioE(Usuario usuarioE) {
        this.usuarioE = usuarioE;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getRecurrencia() {
        return recurrencia;
    }

    public void setRecurrencia(String recurrencia) {
        this.recurrencia = recurrencia;
    }

    public Integer getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(Integer recordatorio) {
        this.recordatorio = recordatorio;
    }
}

package com.fertigApp.backend.payload.response;

public class TareaSugeridaResponse {

    private int id;
    private String criterio;

    public TareaSugeridaResponse() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }
}

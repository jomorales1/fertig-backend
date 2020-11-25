package com.fertigapp.backend.payload.response;

public class SonidoResponse {

    private String sonido;
    private boolean favorite;

    public SonidoResponse() {
        this.sonido = "";
        this.favorite = false;
    }

    public String getSonido() {
        return sonido;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setSonido(String sonido) {
        this.sonido = sonido;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}

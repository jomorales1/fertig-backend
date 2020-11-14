package com.fertigApp.backend.payload.response;

public class OwnerResponse {

    private String username;
    private String name;
    private boolean isAdmin;

    public OwnerResponse() {
        this.username = "";
        this.name = "";
        this.isAdmin = false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

}

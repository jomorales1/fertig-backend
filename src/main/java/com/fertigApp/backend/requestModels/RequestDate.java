package com.fertigApp.backend.requestModels;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class RequestDate implements Serializable {

    private OffsetDateTime fecha;

    public RequestDate() {
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }

    public void setFecha(OffsetDateTime fecha) {
        this.fecha = fecha;
    }
}

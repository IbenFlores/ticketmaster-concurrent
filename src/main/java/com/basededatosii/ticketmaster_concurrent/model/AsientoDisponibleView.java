package com.basededatosii.ticketmaster_concurrent.model;

import java.math.BigDecimal;

public interface AsientoDisponibleView {
    Long getAsientoId();
    String getFila();
    String getNumeroAsiento();
    BigDecimal getPrecio();
    String getZonaNombre();
}

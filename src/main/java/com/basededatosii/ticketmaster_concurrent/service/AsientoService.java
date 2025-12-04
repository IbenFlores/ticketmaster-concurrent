package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;

public interface AsientoService {
    
    Asiento crearAsiento(Asiento asiento);

    Asiento obtenerAsientoPorId(Long id);

    List<Asiento> obtenerAsientosPorZona(Long zonaId);
}
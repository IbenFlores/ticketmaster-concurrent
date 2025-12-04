package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Zona;

public interface ZonaService {

    Zona crearZona(Zona zona);

    Zona obtenerZonaPorId(Long id);

    List<Zona> obtenerZonasPorEvento(Long eventoId);
}
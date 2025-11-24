package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Evento;

public interface EventoService {
    List<Evento> obtenerTodosLosEventos();
    
    Evento obtenerEventoPorId(Long id);
    
    Evento crearEvento(Evento evento);
}
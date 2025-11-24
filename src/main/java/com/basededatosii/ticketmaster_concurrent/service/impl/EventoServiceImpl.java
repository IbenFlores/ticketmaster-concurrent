package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Evento;
import com.basededatosii.ticketmaster_concurrent.repository.EventoRepository;
import com.basededatosii.ticketmaster_concurrent.service.EventoService;

@Service
public class EventoServiceImpl implements EventoService {

    private final EventoRepository eventoRepository;

    public EventoServiceImpl(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evento> obtenerTodosLosEventos() {
        return eventoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Evento obtenerEventoPorId(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
    }

    @Override
    @Transactional
    public Evento crearEvento(Evento evento) {
        return eventoRepository.save(evento);
    }
}
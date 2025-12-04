package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Evento;
import com.basededatosii.ticketmaster_concurrent.model.Zona;
import com.basededatosii.ticketmaster_concurrent.repository.EventoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.ZonaRepository;
import com.basededatosii.ticketmaster_concurrent.service.ZonaService;

@Service
public class ZonaServiceImpl implements ZonaService {

    private final ZonaRepository zonaRepository;
    private final EventoRepository eventoRepository;

    public ZonaServiceImpl(ZonaRepository zonaRepository, EventoRepository eventoRepository) {
        this.zonaRepository = zonaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    @Transactional
    public Zona crearZona(Zona zona) {
        Long eventoId = zona.getEvento().getEventoId();

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Error: No se puede crear la zona. El Evento con ID " + eventoId + " no existe."));

        zona.setEvento(evento);

        return zonaRepository.save(zona);
    }

    @Override
    @Transactional(readOnly = true)
    public Zona obtenerZonaPorId(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Zona> obtenerZonasPorEvento(Long eventoId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new RuntimeException("El Evento ID " + eventoId + " no existe.");
        }
        return zonaRepository.findByEvento_EventoId(eventoId);
    }
}
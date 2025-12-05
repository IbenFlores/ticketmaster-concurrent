package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

import org.hibernate.Hibernate; 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Entrada;
import com.basededatosii.ticketmaster_concurrent.repository.EntradaRepository;
import com.basededatosii.ticketmaster_concurrent.service.EntradaService;

@Service
public class EntradaServiceImpl implements EntradaService {

    private final EntradaRepository entradaRepository;

    public EntradaServiceImpl(EntradaRepository entradaRepository) {
        this.entradaRepository = entradaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Entrada obtenerEntradaPorId(Long id) {
        Entrada entrada = entradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ID: " + id));
        
        Hibernate.initialize(entrada.getAsiento());
        Hibernate.initialize(entrada.getAsiento().getZona());
        Hibernate.initialize(entrada.getAsiento().getZona().getEvento());
        
        return entrada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entrada> listarEntradasPorCompra(Long compraId) {
        List<Entrada> entradas = entradaRepository.findByCompra_CompraId(compraId);
        
        entradas.forEach(e -> {
            Hibernate.initialize(e.getAsiento());
            Hibernate.initialize(e.getAsiento().getZona());
            Hibernate.initialize(e.getAsiento().getZona().getEvento());
        });
        
        return entradas;
    }
}
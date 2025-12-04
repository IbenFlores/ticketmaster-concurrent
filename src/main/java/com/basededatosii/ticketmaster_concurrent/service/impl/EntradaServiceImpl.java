package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

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
        return entradaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrada no encontrada con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entrada> listarEntradasPorCompra(Long compraId) {
        return entradaRepository.findByCompra_CompraId(compraId);
    }
}
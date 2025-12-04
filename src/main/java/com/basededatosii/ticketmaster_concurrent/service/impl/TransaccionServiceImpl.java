package com.basededatosii.ticketmaster_concurrent.service.impl;

import org.springframework.stereotype.Service;

import com.basededatosii.ticketmaster_concurrent.repository.TransaccionRepository;
import com.basededatosii.ticketmaster_concurrent.service.TransaccionService;

@Service
public class TransaccionServiceImpl implements TransaccionService {

    private final TransaccionRepository transaccionRepository;

    public TransaccionServiceImpl(TransaccionRepository transaccionRepository) {
        this.transaccionRepository = transaccionRepository;
    }

    @Override
    public boolean bloquearAsientoYCrearCompra(Long usuarioId, Long asientoId) {
        int filas = transaccionRepository.bloquearAsientoYCrearCompra(usuarioId, asientoId);
        return filas > 0;
    }

    @Override
    public boolean finalizarCompraYVenderAsientos(Long usuarioId, Long eventoId) {
        int filas = transaccionRepository.finalizarCompraYVenderAsientos(usuarioId, eventoId);
        return filas > 0;
    }

    @Override
    public boolean liberarAsientosExpirados() {
        int filas = transaccionRepository.liberarAsientosExpirados();
        return filas > 0;
    }
}

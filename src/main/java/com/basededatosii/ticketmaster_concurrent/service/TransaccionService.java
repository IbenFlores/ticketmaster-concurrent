package com.basededatosii.ticketmaster_concurrent.service;

public interface TransaccionService {

    boolean bloquearAsientoYCrearCompra(Long usuarioId, Long asientoId);

    boolean finalizarCompraYVenderAsientos(Long usuarioId, Long eventoId);

    boolean liberarAsientosExpirados();
}

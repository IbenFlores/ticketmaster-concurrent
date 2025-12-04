package com.basededatosii.ticketmaster_concurrent.service;

import com.basededatosii.ticketmaster_concurrent.model.Compra;

public interface CompraService {
    Compra realizarCompra(Long usuarioId);
}
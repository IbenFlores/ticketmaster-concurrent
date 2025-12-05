package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Compra;

public interface CompraService {
    
    Compra realizarCompra(Long usuarioId);
    
    List<Compra> listarComprasPorUsuario(Long usuarioId);
}
package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Carrito;

public interface CarritoService {
    Carrito agregarItem(Long usuarioId, Long asientoId);

    List<Carrito> obtenerCarritoPorUsuario(Long usuarioId);

    void eliminarItem(Long usuarioId, Long asientoId);
    
    void vaciarCarrito(Long usuarioId);
}
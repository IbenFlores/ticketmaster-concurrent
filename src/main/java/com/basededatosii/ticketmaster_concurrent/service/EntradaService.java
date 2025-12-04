package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.Entrada;

public interface EntradaService {

    Entrada obtenerEntradaPorId(Long id);

    List<Entrada> listarEntradasPorCompra(Long compraId);
}
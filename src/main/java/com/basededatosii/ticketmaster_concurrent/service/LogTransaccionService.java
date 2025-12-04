package com.basededatosii.ticketmaster_concurrent.service;

import java.util.List;

import com.basededatosii.ticketmaster_concurrent.model.LogTransaccion;

public interface LogTransaccionService {
    
    void registrarLog(String accion, Long usuarioId, Long asientoId, Long compraId, String detalles);

    List<LogTransaccion> obtenerTodosLosLogs();
    
    List<LogTransaccion> obtenerLogsPorUsuario(Long usuarioId);
}
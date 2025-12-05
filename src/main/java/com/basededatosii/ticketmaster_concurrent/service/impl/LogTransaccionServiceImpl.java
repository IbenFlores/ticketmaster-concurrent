package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.LogTransaccion;
import com.basededatosii.ticketmaster_concurrent.model.Usuario;
import com.basededatosii.ticketmaster_concurrent.repository.LogTransaccionRepository;
import com.basededatosii.ticketmaster_concurrent.repository.UsuarioRepository;
import com.basededatosii.ticketmaster_concurrent.service.LogTransaccionService;

@Service
public class LogTransaccionServiceImpl implements LogTransaccionService {

    private final LogTransaccionRepository logRepository;
    private final UsuarioRepository usuarioRepository;

    public LogTransaccionServiceImpl(LogTransaccionRepository logRepository, UsuarioRepository usuarioRepository) {
        this.logRepository = logRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW) 
    public void registrarLog(String accion, Long usuarioId, Long asientoId, Long compraId, String detalles) {
        LogTransaccion log = new LogTransaccion();
        log.setAccion(accion);
        log.setAsientoId(asientoId);
        log.setCompraId(compraId);
        log.setDetalles(detalles);

        if (usuarioId != null) {
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            log.setUsuario(usuario);
        }

        logRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogTransaccion> obtenerTodosLosLogs() {
        return logRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LogTransaccion> obtenerLogsPorUsuario(Long usuarioId) {
        return logRepository.findByUsuario_UsuarioId(usuarioId);
    }
}
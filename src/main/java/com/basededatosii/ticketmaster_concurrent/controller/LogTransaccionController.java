package com.basededatosii.ticketmaster_concurrent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.LogTransaccion;
import com.basededatosii.ticketmaster_concurrent.service.LogTransaccionService;

@RestController
@RequestMapping("/api/logs")
public class LogTransaccionController {

    private final LogTransaccionService logService;

    public LogTransaccionController(LogTransaccionService logService) {
        this.logService = logService;
    }

    // GET http://localhost:8080/api/logs
    @GetMapping
    public ResponseEntity<List<LogTransaccion>> verTodosLosLogs() {
        return ResponseEntity.ok(logService.obtenerTodosLosLogs());
    }

    // GET http://localhost:8080/api/logs/usuario/{id}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<LogTransaccion>> verLogsDeUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(logService.obtenerLogsPorUsuario(usuarioId));
    }
}
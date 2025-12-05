package com.basededatosii.ticketmaster_concurrent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.Zona;
import com.basededatosii.ticketmaster_concurrent.service.ZonaService;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    // POST /api/zonas
    @PostMapping
    public ResponseEntity<Zona> crearZona(@RequestBody Zona zona) {
        return ResponseEntity.ok(zonaService.crearZona(zona));
    }

    // GET /api/zonas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Zona> obtenerZona(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(zonaService.obtenerZonaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/zonas/evento/{eventoId}
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<Zona>> listarZonasPorEvento(@PathVariable Long eventoId) {
        try {
            return ResponseEntity.ok(zonaService.obtenerZonasPorEvento(eventoId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
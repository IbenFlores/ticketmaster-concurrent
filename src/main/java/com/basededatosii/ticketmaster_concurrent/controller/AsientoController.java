package com.basededatosii.ticketmaster_concurrent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.service.AsientoService;

@RestController
@RequestMapping("/api/asientos")
public class AsientoController {

    private final AsientoService asientoService;

    public AsientoController(AsientoService asientoService) {
        this.asientoService = asientoService;
    }

    @PostMapping
    public ResponseEntity<Asiento> crearAsiento(@RequestBody Asiento asiento) {
        return ResponseEntity.ok(asientoService.crearAsiento(asiento));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asiento> obtenerAsiento(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(asientoService.obtenerAsientoPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/zona/{zonaId}")
    public ResponseEntity<List<Asiento>> listarAsientosPorZona(@PathVariable Long zonaId) {
        return ResponseEntity.ok(asientoService.obtenerAsientosPorZona(zonaId));
    }
}
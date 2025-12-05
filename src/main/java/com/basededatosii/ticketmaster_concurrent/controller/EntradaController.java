package com.basededatosii.ticketmaster_concurrent.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.Entrada;
import com.basededatosii.ticketmaster_concurrent.service.EntradaService;

@RestController
@RequestMapping("/api/entradas")
public class EntradaController {

    private final EntradaService entradaService;

    public EntradaController(EntradaService entradaService) {
        this.entradaService = entradaService;
    }

    // GET /api/entradas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Entrada> obtenerEntrada(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(entradaService.obtenerEntradaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/entradas/compra/{compraId}
    @GetMapping("/compra/{compraId}")
    public ResponseEntity<List<Entrada>> listarEntradasPorCompra(@PathVariable Long compraId) {
        return ResponseEntity.ok(entradaService.listarEntradasPorCompra(compraId));
    }
}
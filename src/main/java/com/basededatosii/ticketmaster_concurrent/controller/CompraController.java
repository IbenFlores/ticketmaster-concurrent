package com.basededatosii.ticketmaster_concurrent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.Compra;
import com.basededatosii.ticketmaster_concurrent.service.CompraService;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    // POST http://localhost:8080/api/compras/checkout/{usuarioId}
    @PostMapping("/checkout/{usuarioId}")
    public ResponseEntity<?> realizarCheckout(@PathVariable Long usuarioId) {
        try {
            Compra compra = compraService.realizarCompra(usuarioId);
            return ResponseEntity.ok(compra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
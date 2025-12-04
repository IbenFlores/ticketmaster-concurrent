package com.basededatosii.ticketmaster_concurrent.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.model.Carrito;
import com.basededatosii.ticketmaster_concurrent.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    // POST /api/carritos/agregar
    // Body: { "usuarioId": 1, "asientoId": 5 }
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestBody Map<String, Long> payload) {
        try {
            Long usuarioId = payload.get("usuarioId");
            Long asientoId = payload.get("asientoId");
            Carrito nuevoItem = carritoService.agregarItem(usuarioId, asientoId);
            return ResponseEntity.ok(nuevoItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/carritos/{usuarioId}
    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Carrito>> verCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarritoPorUsuario(usuarioId));
    }

    // DELETE /api/carritos/limpiar/{usuarioId}
    @DeleteMapping("/limpiar/{usuarioId}")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.ok("Carrito vaciado y asientos liberados.");
    }
}
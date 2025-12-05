package com.basededatosii.ticketmaster_concurrent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basededatosii.ticketmaster_concurrent.service.TransaccionService;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // POST /transacciones/bloquear-asiento/{asientoId}?usuarioId=1
    @PostMapping("/bloquear-asiento/{asientoId}")
    public ResponseEntity<?> bloquearAsiento(
            @PathVariable Long asientoId,
            @RequestParam Long usuarioId
    ) {
        boolean exito = transaccionService.bloquearAsientoYCrearCompra(usuarioId, asientoId);

        if (exito) {
            return ResponseEntity.ok("Asiento bloqueado y agregado al carrito exitosamente.");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo bloquear el asiento (ya está vendido, bloqueado o no existe).");
        }
    }

    // POST /transacciones/finalizar-compra?usuarioId=1
    @PostMapping("/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(
            @RequestParam Long usuarioId
    ) {
        boolean exito = transaccionService.finalizarCompraGlobal(usuarioId);

        if (exito) {
            return ResponseEntity.ok("Compra global finalizada correctamente. Entradas generadas.");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se encontraron asientos bloqueados en el carrito para procesar.");
        }
    }

    // POST /transacciones/liberar-expirados
    @PostMapping("/liberar-expirados")
    public ResponseEntity<?> liberarExpirados() {
        boolean exito = transaccionService.liberarAsientosExpirados();

        if (exito) {
            return ResponseEntity.ok("Se liberaron los asientos expirados.");
        } else {
            return ResponseEntity.ok("No había asientos expirados para liberar.");
        }
    }
}
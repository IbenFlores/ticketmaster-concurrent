package com.basededatosii.ticketmaster_concurrent.controller;

import com.basededatosii.ticketmaster_concurrent.service.TransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    // POST /transacciones/bloquear-asiento/1?usuarioId=1
    @PostMapping("/bloquear-asiento/{asientoId}")
    public ResponseEntity<?> bloquearAsiento(
            @PathVariable Long asientoId,
            @RequestParam Long usuarioId
    ) {
        boolean exito = transaccionService.bloquearAsientoYCrearCompra(usuarioId, asientoId);

        if (exito) {
            return ResponseEntity.ok("Asiento bloqueado y compra PENDIENTE creada.");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se pudo bloquear el asiento (ya está vendido o bloqueado).");
        }
    }

    // POST /transacciones/finalizar-compra?usuarioId=1&eventoId=1
    @PostMapping("/finalizar-compra")
    public ResponseEntity<?> finalizarCompra(
            @RequestParam Long usuarioId,
            @RequestParam Long eventoId
    ) {
        boolean exito = transaccionService.finalizarCompraYVenderAsientos(usuarioId, eventoId);

        if (exito) {
            return ResponseEntity.ok("Compra finalizada, entradas generadas y asientos marcados como VENDIDO.");
        } else {
            return ResponseEntity.badRequest()
                    .body("No se encontró compra PENDIENTE para ese usuario y evento.");
        }
    }

        // POST /transacciones/liberar-expirados
    @PostMapping("/liberar-expirados")
    public ResponseEntity<?> liberarExpirados() {
        boolean exito = transaccionService.liberarAsientosExpirados();

        if (exito) {
            return ResponseEntity.ok("Asientos expirados liberados correctamente.");
        } else {
            return ResponseEntity.ok("No había asientos expirados para liberar.");
        }
    }

}

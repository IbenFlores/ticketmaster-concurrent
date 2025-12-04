package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.Carrito;
import com.basededatosii.ticketmaster_concurrent.model.Compra;
import com.basededatosii.ticketmaster_concurrent.model.Entrada;
import com.basededatosii.ticketmaster_concurrent.model.Evento;
import com.basededatosii.ticketmaster_concurrent.model.Usuario;
import com.basededatosii.ticketmaster_concurrent.repository.AsientoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.CarritoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.CompraRepository;
import com.basededatosii.ticketmaster_concurrent.repository.UsuarioRepository;
import com.basededatosii.ticketmaster_concurrent.service.CompraService;
import com.basededatosii.ticketmaster_concurrent.service.LogTransaccionService;

@Service
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsientoRepository asientoRepository;
    private final LogTransaccionService logService;

    public CompraServiceImpl(CompraRepository compraRepository,
                             CarritoRepository carritoRepository,
                             UsuarioRepository usuarioRepository,
                             AsientoRepository asientoRepository,
                             LogTransaccionService logService) {
        this.compraRepository = compraRepository;
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asientoRepository = asientoRepository;
        this.logService = logService;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Compra realizarCompra(Long usuarioId) {
        
        // 1. Validar Usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Obtener items del carrito
        List<Carrito> itemsCarrito = carritoRepository.findByUsuario_UsuarioId(usuarioId);
        
        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        // 3. Crear cabecera de Compra
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        
        // Asignamos el evento basándonos en el primer item (asumiendo compra simple)
        Evento evento = itemsCarrito.get(0).getAsiento().getZona().getEvento();
        compra.setEvento(evento);

        BigDecimal total = BigDecimal.ZERO;
        List<Entrada> entradasParaGuardar = new ArrayList<>();

        // 4. Procesar items
        for (Carrito item : itemsCarrito) {
            Asiento asiento = item.getAsiento();

            // Verificamos que el asiento siga reservado por seguridad
            // OJO: Al usar Isolation.SERIALIZABLE, esto es muy seguro.
            if (!"RESERVADO".equals(asiento.getEstado())) {
                throw new RuntimeException("Error: El asiento " + asiento.getNumeroAsiento() + " no está reservado correctamente.");
            }

            // Crear Entrada
            Entrada entrada = new Entrada();
            entrada.setCompra(compra);
            entrada.setAsiento(asiento);
            entrada.setPrecio(item.getPrecio()); // Usamos el precio que se guardó en el carrito
            
            entradasParaGuardar.add(entrada);

            total = total.add(item.getPrecio());

            // Marcar asiento como VENDIDO
            asiento.setEstado("VENDIDO");
            asientoRepository.save(asiento);
        }

        compra.setMontoTotal(total);
        compra.setEntradas(entradasParaGuardar);

        // 5. Guardar Compra (Cascade guarda las Entradas)
        Compra compraGuardada = compraRepository.save(compra);

        // 6. Limpiar Carrito
        carritoRepository.deleteAll(itemsCarrito);

        logService.registrarLog("COMPRA_EXITOSA", usuarioId, null, compraGuardada.getCompraId(), "Monto: " + compraGuardada.getMontoTotal());
        return compraGuardada;
    }
}
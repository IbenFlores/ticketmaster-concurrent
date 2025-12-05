package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.Carrito;
import com.basededatosii.ticketmaster_concurrent.model.Usuario;
import com.basededatosii.ticketmaster_concurrent.repository.AsientoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.CarritoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.UsuarioRepository;
import com.basededatosii.ticketmaster_concurrent.service.CarritoService;

@Service
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsientoRepository asientoRepository;

    public CarritoServiceImpl(CarritoRepository carritoRepository, 
                              UsuarioRepository usuarioRepository, 
                              AsientoRepository asientoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asientoRepository = asientoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Carrito> obtenerCarritoPorUsuario(Long usuarioId) {
        List<Carrito> items = carritoRepository.findByUsuario_UsuarioId(usuarioId);
        
        items.forEach(c -> {
            Hibernate.initialize(c.getAsiento());
            Hibernate.initialize(c.getAsiento().getZona());
            Hibernate.initialize(c.getAsiento().getZona().getEvento());
        });
        
        return items;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE) 
    public Carrito agregarItem(Long usuarioId, Long asientoId) {
        
        // Validaciones básicas
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Asiento asiento = asientoRepository.findById(asientoId)
                .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!"DISPONIBLE".equals(asiento.getEstado())) {
            throw new RuntimeException("El asiento no está disponible para reserva.");
        }

        if (carritoRepository.existsByAsiento_AsientoId(asientoId)) {
            throw new RuntimeException("El asiento ya está reservado en otro carrito.");
        }

        // Crear Item
        Carrito item = new Carrito();
        item.setUsuario(usuario);
        item.setAsiento(asiento);
        
        // Usamos el precio real del asiento (actualizado según tu esquema V1)
        BigDecimal precioAsiento = asiento.getPrecio() != null ? asiento.getPrecio() : new BigDecimal("100.00");
        item.setPrecio(precioAsiento);

        // Bloquear Asiento (Esta es la lógica Java simple, útil si no usas el TransaccionService)
        asiento.setEstado("RESERVADO");
        asientoRepository.save(asiento);

        return carritoRepository.save(item);
    }

    @Override
    @Transactional
    public void eliminarItem(Long usuarioId, Long asientoId) {
        Carrito item = carritoRepository.findByUsuario_UsuarioIdAndAsiento_AsientoId(usuarioId, asientoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));
        
        // Liberar el asiento y limpiar datos de bloqueo
        Asiento asiento = item.getAsiento();
        asiento.setEstado("DISPONIBLE");
        asiento.setUsuarioBloqueoId(null);
        asiento.setFechaBloqueo(null);
        
        asientoRepository.save(asiento);
        
        carritoRepository.delete(item);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        List<Carrito> items = carritoRepository.findByUsuario_UsuarioId(usuarioId);
        
        // Liberar todos los asientos del carrito
        for (Carrito item : items) {
            Asiento asiento = item.getAsiento();
            asiento.setEstado("DISPONIBLE");
            asiento.setUsuarioBloqueoId(null);
            asiento.setFechaBloqueo(null);
            asientoRepository.save(asiento);
        }
        
        carritoRepository.deleteByUsuario_UsuarioId(usuarioId);
    }
}
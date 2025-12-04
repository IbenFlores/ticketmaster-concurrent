package com.basededatosii.ticketmaster_concurrent.service.impl;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.Carrito;
import com.basededatosii.ticketmaster_concurrent.model.Usuario;
import com.basededatosii.ticketmaster_concurrent.repository.AsientoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.CarritoRepository;
import com.basededatosii.ticketmaster_concurrent.repository.UsuarioRepository;
import com.basededatosii.ticketmaster_concurrent.service.CarritoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
    @Transactional(isolation = Isolation.SERIALIZABLE) 
    public Carrito agregarItem(Long usuarioId, Long asientoId) {
        
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

        Carrito item = new Carrito();
        item.setUsuario(usuario);
        item.setAsiento(asiento);
        
        // Precio temporal fijo ya que Zona no tiene precio
        item.setPrecio(new BigDecimal("100.00")); 

        asiento.setEstado("RESERVADO");
        asientoRepository.save(asiento);

        return carritoRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Carrito> obtenerCarritoPorUsuario(Long usuarioId) {
        return carritoRepository.findByUsuario_UsuarioId(usuarioId);
    }

    @Override
    @Transactional
    public void eliminarItem(Long usuarioId, Long asientoId) {
        Carrito item = carritoRepository.findByUsuario_UsuarioIdAndAsiento_AsientoId(usuarioId, asientoId)
                .orElseThrow(() -> new RuntimeException("Item no encontrado en el carrito"));
        
        Asiento asiento = item.getAsiento();
        asiento.setEstado("DISPONIBLE");
        asientoRepository.save(asiento);
        
        carritoRepository.delete(item);
    }

    @Override
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        List<Carrito> items = carritoRepository.findByUsuario_UsuarioId(usuarioId);
        for (Carrito item : items) {
            Asiento asiento = item.getAsiento();
            asiento.setEstado("DISPONIBLE");
            asientoRepository.save(asiento);
        }
        carritoRepository.deleteByUsuario_UsuarioId(usuarioId);
    }
}
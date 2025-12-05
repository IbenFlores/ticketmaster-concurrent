package com.basededatosii.ticketmaster_concurrent.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
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

@Service
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsientoRepository asientoRepository;

    public CompraServiceImpl(CompraRepository compraRepository,
                             CarritoRepository carritoRepository,
                             UsuarioRepository usuarioRepository,
                             AsientoRepository asientoRepository) {
        this.compraRepository = compraRepository;
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.asientoRepository = asientoRepository;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Compra realizarCompra(Long usuarioId) {
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Carrito> itemsCarrito = carritoRepository.findByUsuario_UsuarioId(usuarioId);
        
        if (itemsCarrito.isEmpty()) {
            throw new RuntimeException("El carrito está vacío.");
        }

        Compra compra = new Compra();
        compra.setUsuario(usuario);
        
        Evento evento = itemsCarrito.get(0).getAsiento().getZona().getEvento();
        compra.setEvento(evento);

        BigDecimal total = BigDecimal.ZERO;
        List<Entrada> entradasParaGuardar = new ArrayList<>();

        for (Carrito item : itemsCarrito) {
            Asiento asiento = item.getAsiento();

            if (!"RESERVADO".equals(asiento.getEstado())) {
                throw new RuntimeException("Error: El asiento " + asiento.getNumeroAsiento() + " no está reservado correctamente.");
            }

            Entrada entrada = new Entrada();
            entrada.setCompra(compra);
            entrada.setAsiento(asiento);
            entrada.setPrecio(item.getPrecio());
            
            entradasParaGuardar.add(entrada);
            total = total.add(item.getPrecio());

            asiento.setEstado("VENDIDO");
            asientoRepository.save(asiento);
        }

        compra.setMontoTotal(total);
        compra.setEntradas(entradasParaGuardar);

        Compra compraGuardada = compraRepository.save(compra);
        carritoRepository.deleteAll(itemsCarrito);

        return compraGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Compra> listarComprasPorUsuario(Long usuarioId) {
        List<Compra> todasLasCompras = compraRepository.findByUsuario_UsuarioId(usuarioId);
        List<Compra> comprasCompletadas = new ArrayList<>();

        for (Compra c : todasLasCompras) {
            if ("COMPLETADA".equals(c.getEstado())) {
                Hibernate.initialize(c.getEvento());
                comprasCompletadas.add(c);
            }
        }
        
        return comprasCompletadas;
    }
}
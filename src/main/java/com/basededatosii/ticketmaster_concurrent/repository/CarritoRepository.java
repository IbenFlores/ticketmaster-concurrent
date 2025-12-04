package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Carrito;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    List<Carrito> findByUsuario_UsuarioId(Long usuarioId);

    boolean existsByAsiento_AsientoId(Long asientoId);
    
    Optional<Carrito> findByUsuario_UsuarioIdAndAsiento_AsientoId(Long usuarioId, Long asientoId);
    
    void deleteByUsuario_UsuarioId(Long usuarioId);
}
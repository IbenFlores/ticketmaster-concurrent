package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByUsuario_UsuarioId(Long usuarioId);
}
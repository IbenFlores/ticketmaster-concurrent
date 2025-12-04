package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Long> {
    
    List<Entrada> findByCompra_CompraId(Long compraId);
}
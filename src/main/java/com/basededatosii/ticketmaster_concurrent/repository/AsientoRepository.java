package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    
    List<Asiento> findByZona_ZonaId(Long zonaId);
}
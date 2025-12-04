package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Zona;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Long> {
    List<Zona> findByEvento_EventoId(Long eventoId);
}
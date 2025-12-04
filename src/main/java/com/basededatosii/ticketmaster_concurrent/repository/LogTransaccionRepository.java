package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.LogTransaccion;

@Repository
public interface LogTransaccionRepository extends JpaRepository<LogTransaccion, Long> {
    List<LogTransaccion> findByUsuario_UsuarioId(Long usuarioId);
}
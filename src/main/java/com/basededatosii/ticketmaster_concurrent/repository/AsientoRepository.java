package com.basededatosii.ticketmaster_concurrent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.basededatosii.ticketmaster_concurrent.model.Asiento;
import com.basededatosii.ticketmaster_concurrent.model.AsientoDisponibleView;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {
    
    List<Asiento> findByZona_ZonaId(Long zonaId);

        @Query(
        value = """
            SELECT
                a.asiento_id AS asientoId,
                a.fila AS fila,
                a.numero_asiento AS numeroAsiento,
                a.precio AS precio,
                z.nombre AS zonaNombre
            FROM
                asientos a
            JOIN
                zonas z ON a.zona_id = z.zona_id
            WHERE
                 a.estado = 'DISPONIBLE'
                AND a.usuario_bloqueo_id IS NULL
            ORDER BY a.precio ASC
            """,
        nativeQuery = true
    )
    List<AsientoDisponibleView> findAsientosDisponibles();
}

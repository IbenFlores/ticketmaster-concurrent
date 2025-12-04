package com.basededatosii.ticketmaster_concurrent.repository;

import com.basededatosii.ticketmaster_concurrent.model.Compra;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TransaccionRepository extends JpaRepository<Compra, Long> {

    // 1) Bloquear asiento y crear compra PENDIENTE
    @Modifying
    @Transactional
    @Query(
        value = """
            WITH asiento_bloqueado AS (
                UPDATE asientos a
                SET 
                    usuario_bloqueo_id = :usuarioId,
                    fecha_bloqueo      = NOW(),
                    estado             = 'BLOQUEADO'
                WHERE 
                    a.asiento_id = :asientoId
                    AND a.estado = 'DISPONIBLE'
                    AND a.usuario_bloqueo_id IS NULL
                RETURNING 
                    a.asiento_id,
                    :usuarioId AS usuario_id
            ),
            nueva_compra AS (
                INSERT INTO compras (usuario_id, evento_id, monto_total, estado)
                SELECT
                    ab.usuario_id,
                    z.evento_id,
                    a.precio AS monto_total,
                    'PENDIENTE' AS estado
                FROM asiento_bloqueado ab
                JOIN asientos a ON a.asiento_id = ab.asiento_id
                JOIN zonas   z ON a.zona_id = z.zona_id
                RETURNING compra_id, usuario_id
            )
            INSERT INTO logs_transacciones (usuario_id, accion, asiento_id, compra_id, detalles)
            SELECT
                :usuarioId AS usuario_id,
                CASE 
                    WHEN EXISTS (SELECT 1 FROM asiento_bloqueado)
                        THEN 'BLOQUEO_EXITOSO'
                    ELSE 'BLOQUEO_FALLIDO'
                END AS accion,
                :asientoId AS asiento_id,
                nc.compra_id,
                'Intento de bloqueo para agregar al carrito.' AS detalles
            FROM (SELECT :usuarioId AS usuario_id, :asientoId AS asiento_id) p
            LEFT JOIN nueva_compra nc ON nc.usuario_id = p.usuario_id
            """,
        nativeQuery = true
    )
    int bloquearAsientoYCrearCompra(
            @Param("usuarioId") Long usuarioId,
            @Param("asientoId") Long asientoId
    );

    // 2) Finalizar compra y vender asientos
    @Modifying
    @Transactional
    @Query(
        value = """
            WITH compra_pendiente AS (
                SELECT compra_id
                FROM compras
                WHERE usuario_id = :usuarioId
                  AND evento_id  = :eventoId
                  AND estado     = 'PENDIENTE'
                ORDER BY fecha_creacion DESC
                LIMIT 1
            ),
            compra_actualizada AS (
                UPDATE compras c
                SET estado = 'COMPLETADA'
                FROM compra_pendiente cp
                WHERE c.compra_id = cp.compra_id
                RETURNING c.compra_id
            ),
            entradas_insertadas AS (
                INSERT INTO entradas (asiento_id, compra_id, precio)
                SELECT
                    a.asiento_id,
                    ca.compra_id,
                    a.precio
                FROM asientos a
                JOIN zonas z ON z.zona_id = a.zona_id
                JOIN compra_actualizada ca ON z.evento_id = :eventoId
                WHERE
                    a.usuario_bloqueo_id = :usuarioId
                    AND a.estado = 'BLOQUEADO'
                RETURNING asiento_id, compra_id
            ),
            logs_insertados AS (
                INSERT INTO logs_transacciones (usuario_id, accion, asiento_id, compra_id, detalles)
                SELECT
                    :usuarioId,
                    'COMPRA_EXITOSA',
                    e.asiento_id,
                    e.compra_id,
                    'Compra finalizada. Asiento vendido y entrada generada.'
                FROM entradas_insertadas e
                RETURNING asiento_id
            )
            UPDATE asientos a
            SET
                estado = 'VENDIDO',
                usuario_bloqueo_id = NULL,
                fecha_bloqueo = NULL
            FROM zonas z
            WHERE 
                a.zona_id = z.zona_id
                AND a.usuario_bloqueo_id = :usuarioId
                AND a.estado = 'BLOQUEADO'
                AND z.evento_id = :eventoId
                AND EXISTS (SELECT 1 FROM compra_actualizada);
            """,
        nativeQuery = true
    )
    int finalizarCompraYVenderAsientos(
            @Param("usuarioId") Long usuarioId,
            @Param("eventoId") Long eventoId
    );

    // 3) Liberar asientos expirados
    @Modifying
    @Transactional
    @Query(
        value = """
            WITH asientos_liberados AS (
                UPDATE asientos
                SET
                    estado = 'DISPONIBLE',
                    usuario_bloqueo_id = NULL,
                    fecha_bloqueo = NULL
                WHERE
                    usuario_bloqueo_id IS NOT NULL
                    AND estado = 'BLOQUEADO'
                    AND fecha_bloqueo IS NOT NULL
                    AND fecha_bloqueo < NOW() - INTERVAL '15 minutes'
                RETURNING usuario_bloqueo_id, asiento_id
            )
            INSERT INTO logs_transacciones (usuario_id, accion, asiento_id, detalles)
            SELECT
                usuario_bloqueo_id,
                'TIMEOUT_LIBERADO',
                asiento_id,
                'Asiento liberado automáticamente. Timeout de 15 minutos expirado.'
            FROM asientos_liberados;
            """,
        nativeQuery = true
    )
    int liberarAsientosExpirados();
}

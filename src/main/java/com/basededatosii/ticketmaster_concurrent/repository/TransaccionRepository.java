package com.basededatosii.ticketmaster_concurrent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.basededatosii.ticketmaster_concurrent.model.Compra;

@Repository
public interface TransaccionRepository extends JpaRepository<Compra, Long> {

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
                    a.precio,
                    :usuarioId AS usuario_id
            ),
            insertar_carrito AS (
                INSERT INTO carritos (usuario_id, asiento_id, precio, fecha_creacion)
                SELECT 
                    ab.usuario_id, 
                    ab.asiento_id, 
                    ab.precio, 
                    NOW()
                FROM asiento_bloqueado ab
                RETURNING carrito_id
            ),
            nueva_compra AS (
                INSERT INTO compras (usuario_id, evento_id, monto_total, estado)
                SELECT
                    ab.usuario_id,
                    z.evento_id,
                    ab.precio AS monto_total,
                    'PENDIENTE' AS estado
                FROM asiento_bloqueado ab
                JOIN asientos a ON a.asiento_id = ab.asiento_id
                JOIN zonas   z ON a.zona_id = z.zona_id
                RETURNING compra_id, usuario_id
            )
            INSERT INTO logs_transacciones (usuario_id, accion, asiento_id, compra_id, detalles)
            SELECT
                :usuarioId,
                CASE 
                    WHEN EXISTS (SELECT 1 FROM asiento_bloqueado)
                        THEN 'BLOQUEO_EXITOSO'
                    ELSE 'BLOQUEO_FALLIDO'
                END,
                :asientoId,
                nc.compra_id,
                'Asiento bloqueado y agregado al carrito.'
            FROM (SELECT :usuarioId AS usuario_id) p
            LEFT JOIN nueva_compra nc ON nc.usuario_id = p.usuario_id
            """,
        nativeQuery = true
    )
    int bloquearAsientoYCrearCompra(
            @Param("usuarioId") Long usuarioId,
            @Param("asientoId") Long asientoId
    );

    @Modifying
    @Transactional
    @Query(
        value = """
            WITH asientos_a_comprar AS (
                SELECT asiento_id, precio
                FROM asientos
                WHERE usuario_bloqueo_id = :usuarioId
                  AND estado = 'BLOQUEADO'
            ),
            compra_maestra AS (
                SELECT compra_id
                FROM compras
                WHERE usuario_id = :usuarioId
                  AND estado = 'PENDIENTE'
                ORDER BY fecha_creacion DESC
                LIMIT 1
            ),
            total_calculado AS (
                SELECT SUM(precio) as total FROM asientos_a_comprar
            ),
            actualizar_compra AS (
                UPDATE compras c
                SET 
                    estado = 'COMPLETADA',
                    monto_total = (SELECT total FROM total_calculado),
                    fecha_creacion = NOW()
                WHERE c.compra_id = (SELECT compra_id FROM compra_maestra)
                RETURNING c.compra_id
            ),
            crear_entradas AS (
                INSERT INTO entradas (asiento_id, compra_id, precio)
                SELECT
                    ac.asiento_id,
                    (SELECT compra_id FROM actualizar_compra),
                    ac.precio
                FROM asientos_a_comprar ac
                RETURNING entrada_id
            ),
            limpiar_carrito AS (
                DELETE FROM carritos 
                WHERE usuario_id = :usuarioId
            ),
            limpiar_compras_pendientes_sobrantes AS (
                DELETE FROM compras
                WHERE usuario_id = :usuarioId
                  AND estado = 'PENDIENTE'
                  AND compra_id != (SELECT compra_id FROM compra_maestra)
            ),
            registrar_log AS (
                INSERT INTO logs_transacciones (usuario_id, accion, compra_id, detalles)
                SELECT
                    :usuarioId,
                    'COMPRA_EXITOSA',
                    (SELECT compra_id FROM actualizar_compra),
                    'Checkout global completado. Entradas generadas.'
                WHERE EXISTS (SELECT 1 FROM actualizar_compra)
            )
            UPDATE asientos a
            SET
                estado = 'VENDIDO',
                usuario_bloqueo_id = NULL,
                fecha_bloqueo = NULL
            WHERE a.asiento_id IN (SELECT asiento_id FROM asientos_a_comprar);
            """,
        nativeQuery = true
    )
    int finalizarCompraGlobal(@Param("usuarioId") Long usuarioId);

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
            ),
            limpiar_carritos_expirados AS (
                DELETE FROM carritos
                WHERE asiento_id IN (SELECT asiento_id FROM asientos_liberados)
            )
            INSERT INTO logs_transacciones (usuario_id, accion, asiento_id, detalles)
            SELECT
                usuario_bloqueo_id,
                'TIMEOUT_LIBERADO',
                asiento_id,
                'Asiento liberado automáticamente. Timeout expirado.'
            FROM asientos_liberados;
            """,
        nativeQuery = true
    )
    int liberarAsientosExpirados();
}
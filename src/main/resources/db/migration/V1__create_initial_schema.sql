-- 1. USUARIOS
CREATE TABLE usuarios (
    usuario_id BIGSERIAL PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contrasena_hash TEXT NOT NULL
);

-- 2. EVENTOS
CREATE TABLE eventos (
    evento_id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_evento TIMESTAMP NOT NULL,
    lugar VARCHAR(150),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- 3. ZONAS
CREATE TABLE zonas (
    zona_id BIGSERIAL PRIMARY KEY,
    evento_id BIGINT NOT NULL REFERENCES eventos(evento_id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL
);

-- 4. ASIENTOS
CREATE TABLE asientos (
    asiento_id BIGSERIAL PRIMARY KEY,
    zona_id BIGINT NOT NULL REFERENCES zonas(zona_id) ON DELETE CASCADE,
    fila VARCHAR(10),
    numero_asiento VARCHAR(10),
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    version INT DEFAULT 0
);

-- 5. CARRITOS
CREATE TABLE carritos (
    carrito_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(usuario_id) ON DELETE CASCADE,
    asiento_id BIGINT NOT NULL REFERENCES asientos(asiento_id) ON DELETE CASCADE,
    precio DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT uq_carrito_asiento UNIQUE (asiento_id)
);

-- 6. COMPRAS
CREATE TABLE compras (
    compra_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(usuario_id),
    evento_id BIGINT REFERENCES eventos(evento_id),
    monto_total DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

-- 7. ENTRADAS
CREATE TABLE entradas (
    entrada_id BIGSERIAL PRIMARY KEY,
    compra_id BIGINT REFERENCES compras(compra_id) ON DELETE CASCADE,
    asiento_id BIGINT NOT NULL REFERENCES asientos(asiento_id),
    precio DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT uq_entrada_asiento UNIQUE (asiento_id)
);

-- 8. LOGS DE TRANSACCIONES
CREATE TABLE logs_transacciones (
    log_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES usuarios(usuario_id),
    accion VARCHAR(50) NOT NULL,
    asiento_id BIGINT,
    compra_id BIGINT,
    detalles TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW()
);

-- ==========================================
-- ÍNDICES
-- ==========================================
CREATE INDEX idx_eventos_fecha_evento ON eventos (fecha_evento);
CREATE INDEX idx_eventos_nombre ON eventos (nombre);
CREATE INDEX idx_zonas_evento_id ON zonas (evento_id);
CREATE INDEX idx_asientos_zona_id ON asientos (zona_id);
CREATE INDEX idx_asientos_estado ON asientos (estado);

CREATE UNIQUE INDEX uix_asientos_zona_fila_numero ON asientos (zona_id, fila, numero_asiento);

CREATE INDEX idx_carritos_usuario_id ON carritos (usuario_id);
CREATE INDEX idx_carritos_asiento_id ON carritos (asiento_id);

CREATE INDEX idx_compras_usuario_id ON compras (usuario_id);
CREATE INDEX idx_compras_evento_id ON compras (evento_id);
CREATE INDEX idx_logs_usuario_id ON logs_transacciones (usuario_id);
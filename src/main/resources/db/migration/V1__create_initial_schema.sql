-- Tabla de Usuarios
CREATE TABLE usuarios (
    usuario_id BIGSERIAL PRIMARY KEY, 
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contrasena_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'CLIENTE'
);

-- Tabla de Eventos
CREATE TABLE eventos (
    evento_id BIGSERIAL PRIMARY KEY, 
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_evento TIMESTAMP NOT NULL,
    lugar VARCHAR(150),
    estado VARCHAR(20) DEFAULT 'ACTIVO'
);

-- Tabla de Zonas 
CREATE TABLE zonas (
    zona_id BIGSERIAL PRIMARY KEY, 
    evento_id BIGINT NOT NULL REFERENCES eventos(evento_id) ON DELETE CASCADE, 
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL  
);

-- Tabla de asientos 
CREATE TABLE asientos (
    asiento_id BIGSERIAL PRIMARY KEY, 
    zona_id BIGINT NOT NULL REFERENCES zonas(zona_id) ON DELETE CASCADE, 
    fila VARCHAR(10),
    numero_asiento VARCHAR(10),
    precio DECIMAL(10,2) NOT NULL,  
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    usuario_bloqueo_id BIGINT REFERENCES usuarios(usuario_id), 
    fecha_bloqueo TIMESTAMP, 
    version INT DEFAULT 0,
    UNIQUE (zona_id, fila, numero_asiento)
);

-- Tabla de Carritos
CREATE TABLE carritos (
    carrito_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(usuario_id) ON DELETE CASCADE,
    asiento_id BIGINT NOT NULL REFERENCES asientos(asiento_id) ON DELETE CASCADE,
    precio DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT uq_carrito_asiento UNIQUE (asiento_id)
);

-- Tabla de Compras
CREATE TABLE compras (
    compra_id BIGSERIAL PRIMARY KEY, 
    usuario_id BIGINT REFERENCES usuarios(usuario_id), 
    evento_id BIGINT REFERENCES eventos(evento_id), 
    monto_total DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    estado VARCHAR(20) DEFAULT 'COMPLETADA'
);

-- Tabla de Entradas 
CREATE TABLE entradas (
    entrada_id BIGSERIAL PRIMARY KEY, 
    asiento_id BIGINT UNIQUE NOT NULL REFERENCES asientos(asiento_id) ON DELETE CASCADE, 
    compra_id BIGINT REFERENCES compras(compra_id) ON DELETE CASCADE, 
    precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE logs_transacciones (
    log_id BIGSERIAL PRIMARY KEY, 
    usuario_id BIGINT REFERENCES usuarios(usuario_id), 
    accion VARCHAR(50) NOT NULL,  
    asiento_id BIGINT, 
    compra_id BIGINT, 
    detalles TEXT,
    fecha_creacion TIMESTAMP DEFAULT NOW()
); 

-- Índices Base
CREATE INDEX idx_eventos_fecha_evento ON eventos (fecha_evento);
CREATE INDEX idx_eventos_nombre ON eventos (nombre); 
CREATE INDEX idx_zonas_evento_id ON zonas (evento_id); 
CREATE INDEX idx_asientos_zona_id ON asientos (zona_id); 
CREATE INDEX idx_asientos_estado ON asientos (estado); 
CREATE INDEX idx_compras_usuario_id ON compras (usuario_id); 
CREATE INDEX idx_compras_evento_id ON compras (evento_id);
CREATE INDEX idx_entradas_compra_id ON entradas (compra_id);
CREATE INDEX idx_logs_transacciones_fecha_creacion ON logs_transacciones (fecha_creacion);
CREATE INDEX idx_asientos_zona_estado_precio ON asientos (zona_id, estado, precio);
CREATE INDEX idx_asientos_fecha_bloqueo ON asientos (fecha_bloqueo);
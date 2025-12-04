-- Tabla de Usuarios
CREATE TABLE usuarios (
    usuario_id SERIAL PRIMARY KEY,
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    contrasena_hash TEXT NOT NULL
);

-- Tabla de Eventos
CREATE TABLE eventos (
    evento_id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    fecha_evento TIMESTAMP NOT NULL,
    lugar VARCHAR(150),
    estado VARCHAR(20) DEFAULT 'ACTIVO' -- ACTIVO, CANCELADO, FINALIZADO
);

-- Tabla de Zonas 
CREATE TABLE zonas (
    zona_id SERIAL PRIMARY KEY,
    evento_id INT NOT NULL REFERENCES eventos(evento_id) ON DELETE CASCADE,
    nombre VARCHAR(100) NOT NULL,
    capacidad INT NOT NULL  
);

CREATE TABLE carritos (
    carrito_id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(usuario_id) ON DELETE CASCADE,
    asiento_id BIGINT NOT NULL REFERENCES asientos(asiento_id) ON DELETE CASCADE,
    precio DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    
    CONSTRAINT uq_carrito_asiento UNIQUE (asiento_id)
);


-- Tabla de asientos 
CREATE TABLE asientos (
    asiento_id SERIAL PRIMARY KEY,
    zona_id INT NOT NULL REFERENCES zonas(zona_id) ON DELETE CASCADE,
    fila VARCHAR(10),
    numero_asiento VARCHAR(10),
    precio DECIMAL(10,2) NOT NULL,  
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE', -- DISPONIBLE, VENDIDO
    usuario_bloqueo_id INT REFERENCES usuarios(usuario_id), 
    fecha_bloqueo TIMESTAMP, 
    UNIQUE (zona_id, fila, numero_asiento)
);

-- Tabla de Compras
CREATE TABLE compras (
    compra_id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(usuario_id),
    evento_id INT REFERENCES eventos(evento_id), 
    monto_total DECIMAL(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT NOW(),
    estado VARCHAR(20) DEFAULT 'COMPLETADA' -- COMPLETADA, PENDIENTE, CANCELADA
);

-- Tabla de Entradas 
CREATE TABLE entradas (
    entrada_id SERIAL PRIMARY KEY,
    asiento_id INT UNIQUE NOT NULL REFERENCES asientos(asiento_id) ON DELETE CASCADE, -- Un asiento vendido solo puede tener una entrada
    compra_id INT REFERENCES compras(compra_id) ON DELETE CASCADE,
    precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE logs_transacciones (
    log_id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(usuario_id),
    accion VARCHAR(50) NOT NULL,  
    asiento_id INT,
    compra_id INT,
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
CREATE INDEX idx_logs_usuario_id ON logs_transacciones (usuario_id);
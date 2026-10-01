-- 1. Tabla de personas que entregan el pedido
CREATE TABLE clientes (
                          id UUID PRIMARY KEY,
                          nombres VARCHAR(100),
                          apellidos VARCHAR(100),
                          email VARCHAR(100) UNIQUE,
                          telefono VARCHAR(100),
                          estado_activo BOOLEAN NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE TABLE profesionales (
                          id UUID PRIMARY KEY,
                          nombres VARCHAR(100),
                          apellidos VARCHAR(100),
                          especialidad VARCHAR(100),
                          estado_activo BOOLEAN NOT NULL,
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP
);


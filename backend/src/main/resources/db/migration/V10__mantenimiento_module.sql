-- Mantenimiento module

-- Proveedores
CREATE TABLE proveedores (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    nombre VARCHAR(255) NOT NULL,
    razon_social VARCHAR(255),
    rfc VARCHAR(13),
    especialidad VARCHAR(100),
    telefono VARCHAR(20),
    email VARCHAR(255),
    direccion TEXT,
    notas VARCHAR(500),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for proveedores
CREATE INDEX idx_proveedores_empresa_id ON proveedores(empresa_id);
CREATE INDEX idx_proveedores_especialidad ON proveedores(especialidad);
CREATE INDEX idx_proveedores_activo ON proveedores(activo);

-- Órdenes de Mantenimiento
CREATE TABLE ordenes_mantenimiento (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    numero_orden VARCHAR(50) NOT NULL,
    propiedad_id BIGINT NOT NULL REFERENCES propiedades(id),
    proveedor_id BIGINT REFERENCES proveedores(id),
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    prioridad VARCHAR(20) NOT NULL DEFAULT 'MEDIA',
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud DATE NOT NULL,
    fecha_programada DATE,
    fecha_completada DATE,
    costo_estimado DECIMAL(12, 2),
    costo_final DECIMAL(12, 2),
    notas VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for ordenes_mantenimiento
CREATE INDEX idx_ordenes_mantenimiento_empresa_id ON ordenes_mantenimiento(empresa_id);
CREATE INDEX idx_ordenes_mantenimiento_propiedad_id ON ordenes_mantenimiento(propiedad_id);
CREATE INDEX idx_ordenes_mantenimiento_proveedor_id ON ordenes_mantenimiento(proveedor_id);
CREATE INDEX idx_ordenes_mantenimiento_estado ON ordenes_mantenimiento(estado);
CREATE INDEX idx_ordenes_mantenimiento_prioridad ON ordenes_mantenimiento(prioridad);
CREATE INDEX idx_ordenes_mantenimiento_fecha_solicitud ON ordenes_mantenimiento(fecha_solicitud);

-- Unique constraint for numero_orden per empresa
CREATE UNIQUE INDEX idx_ordenes_numero_orden_empresa ON ordenes_mantenimiento(empresa_id, numero_orden);

-- Seguimiento de Órdenes
CREATE TABLE seguimiento_ordenes (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    orden_id BIGINT NOT NULL REFERENCES ordenes_mantenimiento(id),
    comentario TEXT NOT NULL,
    estado_anterior VARCHAR(20),
    estado_nuevo VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for seguimiento_ordenes
CREATE INDEX idx_seguimiento_ordenes_empresa_id ON seguimiento_ordenes(empresa_id);
CREATE INDEX idx_seguimiento_ordenes_orden_id ON seguimiento_ordenes(orden_id);
CREATE INDEX idx_seguimiento_ordenes_created_at ON seguimiento_ordenes(created_at);

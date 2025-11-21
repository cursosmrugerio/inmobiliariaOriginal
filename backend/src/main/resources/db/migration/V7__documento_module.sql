-- Módulo de Documentos

-- Tabla de documentos
CREATE TABLE documentos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(500),
    tipo_documento VARCHAR(50) NOT NULL,
    tipo_entidad VARCHAR(50) NOT NULL,
    entidad_id BIGINT NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100),
    tamanio BIGINT,
    descripcion TEXT,
    fecha_documento TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documentos_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Índices para documentos
CREATE INDEX idx_documentos_empresa ON documentos(empresa_id);
CREATE INDEX idx_documentos_tipo_entidad ON documentos(tipo_entidad, entidad_id);
CREATE INDEX idx_documentos_tipo_documento ON documentos(tipo_documento);
CREATE INDEX idx_documentos_activo ON documentos(activo);
CREATE INDEX idx_documentos_vencimiento ON documentos(fecha_vencimiento);

-- Comentarios
COMMENT ON TABLE documentos IS 'Almacenamiento de documentos asociados a entidades del sistema';
COMMENT ON COLUMN documentos.tipo_entidad IS 'Tipo de entidad: PERSONA, PROPIEDAD, CONTRATO, PAGO';
COMMENT ON COLUMN documentos.entidad_id IS 'ID de la entidad asociada';

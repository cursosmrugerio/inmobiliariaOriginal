-- Documento Module Migration

-- Documentos table
CREATE TABLE documentos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL,
    tipo_entidad VARCHAR(50) NOT NULL,
    entidad_id BIGINT NOT NULL,
    content_type VARCHAR(100),
    tamano BIGINT,
    ruta_archivo VARCHAR(500) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP,
    creado_por VARCHAR(100),
    CONSTRAINT fk_documento_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Indexes for performance
CREATE INDEX idx_documentos_empresa_id ON documentos(empresa_id);
CREATE INDEX idx_documentos_tipo_entidad ON documentos(tipo_entidad);
CREATE INDEX idx_documentos_entidad_id ON documentos(entidad_id);
CREATE INDEX idx_documentos_tipo_documento ON documentos(tipo_documento);
CREATE INDEX idx_documentos_empresa_entidad ON documentos(empresa_id, tipo_entidad, entidad_id);

-- Comments
COMMENT ON TABLE documentos IS 'Storage for documents associated with various entities';
COMMENT ON COLUMN documentos.tipo_documento IS 'Type: CONTRATO, IDENTIFICACION, COMPROBANTE_DOMICILIO, COMPROBANTE_INGRESOS, ESCRITURA, RECIBO, FACTURA, FOTO, PLANO, AVALUO, OTRO';
COMMENT ON COLUMN documentos.tipo_entidad IS 'Associated entity type: PERSONA, PROPIEDAD, CONTRATO, PAGO, EMPRESA';

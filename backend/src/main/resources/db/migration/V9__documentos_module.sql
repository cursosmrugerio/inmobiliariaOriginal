-- Documentos module

CREATE TABLE documentos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    tipo_documento VARCHAR(30) NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_contenido VARCHAR(100),
    tamano_bytes BIGINT,
    ruta_almacenamiento VARCHAR(1000) NOT NULL,
    entidad_tipo VARCHAR(50),
    entidad_id BIGINT,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_documentos_empresa_id ON documentos(empresa_id);
CREATE INDEX idx_documentos_tipo_documento ON documentos(tipo_documento);
CREATE INDEX idx_documentos_entidad ON documentos(entidad_tipo, entidad_id);
CREATE INDEX idx_documentos_activo ON documentos(activo);

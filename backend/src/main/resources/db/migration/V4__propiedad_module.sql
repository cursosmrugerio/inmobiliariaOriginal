-- Milestone 5: Módulo Propiedades
-- Tablas: cat_tipos_propiedad, propiedades, propiedad_propietario

-- Catálogo de Tipos de Propiedad
CREATE TABLE cat_tipos_propiedad (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT true
);

-- Insertar tipos de propiedad comunes
INSERT INTO cat_tipos_propiedad (nombre, descripcion) VALUES
('CASA', 'Casa habitación'),
('DEPARTAMENTO', 'Departamento o apartamento'),
('LOCAL_COMERCIAL', 'Local comercial'),
('OFICINA', 'Oficina'),
('BODEGA', 'Bodega o almacén'),
('TERRENO', 'Terreno baldío'),
('EDIFICIO', 'Edificio completo'),
('NAVE_INDUSTRIAL', 'Nave industrial');

-- Tabla de Propiedades
CREATE TABLE propiedades (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    tipo_propiedad_id INTEGER NOT NULL REFERENCES cat_tipos_propiedad(id),

    -- Identificación
    nombre VARCHAR(200) NOT NULL,
    clave_catastral VARCHAR(50),

    -- Dirección
    calle VARCHAR(200) NOT NULL,
    numero_exterior VARCHAR(20),
    numero_interior VARCHAR(20),
    estado_id INTEGER REFERENCES cat_estados(id),
    municipio_id INTEGER REFERENCES cat_municipios(id),
    colonia_id INTEGER REFERENCES cat_colonias(id),
    codigo_postal VARCHAR(5),
    referencias TEXT,

    -- Características
    superficie_terreno DECIMAL(10, 2),
    superficie_construccion DECIMAL(10, 2),
    num_recamaras INTEGER,
    num_banos DECIMAL(3, 1),
    num_estacionamientos INTEGER,
    num_pisos INTEGER,
    anio_construccion INTEGER,

    -- Valores
    valor_comercial DECIMAL(15, 2),
    valor_catastral DECIMAL(15, 2),
    renta_mensual DECIMAL(12, 2),

    -- Estado
    disponible BOOLEAN DEFAULT true,
    notas TEXT,

    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_propiedades_empresa ON propiedades(empresa_id);
CREATE INDEX idx_propiedades_tipo ON propiedades(tipo_propiedad_id);
CREATE INDEX idx_propiedades_disponible ON propiedades(disponible);
CREATE INDEX idx_propiedades_colonia ON propiedades(colonia_id);

-- Tabla de relación Propiedad-Propietario (una propiedad puede tener varios propietarios)
CREATE TABLE propiedad_propietario (
    id BIGSERIAL PRIMARY KEY,
    propiedad_id BIGINT NOT NULL REFERENCES propiedades(id) ON DELETE CASCADE,
    propietario_id BIGINT NOT NULL REFERENCES personas(id) ON DELETE CASCADE,
    porcentaje_propiedad DECIMAL(5, 2) DEFAULT 100.00,
    fecha_adquisicion DATE,
    es_principal BOOLEAN DEFAULT false,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(propiedad_id, propietario_id)
);

CREATE INDEX idx_propiedad_propietario_propiedad ON propiedad_propietario(propiedad_id);
CREATE INDEX idx_propiedad_propietario_propietario ON propiedad_propietario(propietario_id);

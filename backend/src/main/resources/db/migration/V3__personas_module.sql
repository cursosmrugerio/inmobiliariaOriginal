-- Milestone 4: Módulo Personas
-- Tablas: personas, persona_rol, direcciones, cuentas_bancarias

-- Tabla de Personas (física/moral)
CREATE TABLE personas (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    tipo_persona VARCHAR(10) NOT NULL CHECK (tipo_persona IN ('FISICA', 'MORAL')),

    -- Persona Física
    nombre VARCHAR(100),
    apellido_paterno VARCHAR(100),
    apellido_materno VARCHAR(100),
    fecha_nacimiento DATE,
    curp VARCHAR(18),

    -- Persona Moral
    razon_social VARCHAR(200),
    nombre_comercial VARCHAR(200),

    -- Común
    rfc VARCHAR(13),
    email VARCHAR(100),
    telefono VARCHAR(20),
    telefono_movil VARCHAR(20),

    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_personas_empresa ON personas(empresa_id);
CREATE INDEX idx_personas_rfc ON personas(rfc);
CREATE INDEX idx_personas_email ON personas(email);
CREATE INDEX idx_personas_tipo ON personas(tipo_persona);

-- Tabla de Roles por Persona (una persona puede ser cliente, propietario, aval, etc.)
CREATE TABLE persona_rol (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL REFERENCES personas(id) ON DELETE CASCADE,
    rol_id INTEGER NOT NULL REFERENCES cat_roles(id),
    fecha_asignacion DATE DEFAULT CURRENT_DATE,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(persona_id, rol_id)
);

CREATE INDEX idx_persona_rol_persona ON persona_rol(persona_id);
CREATE INDEX idx_persona_rol_rol ON persona_rol(rol_id);

-- Tabla de Direcciones
CREATE TABLE direcciones (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL REFERENCES personas(id) ON DELETE CASCADE,
    tipo_direccion VARCHAR(20) NOT NULL DEFAULT 'FISCAL' CHECK (tipo_direccion IN ('FISCAL', 'CORRESPONDENCIA', 'OTRA')),

    calle VARCHAR(200) NOT NULL,
    numero_exterior VARCHAR(20),
    numero_interior VARCHAR(20),

    estado_id INTEGER REFERENCES cat_estados(id),
    municipio_id INTEGER REFERENCES cat_municipios(id),
    colonia_id INTEGER REFERENCES cat_colonias(id),
    codigo_postal VARCHAR(5),

    referencias TEXT,
    es_principal BOOLEAN DEFAULT false,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_direcciones_persona ON direcciones(persona_id);
CREATE INDEX idx_direcciones_colonia ON direcciones(colonia_id);

-- Tabla de Cuentas Bancarias
CREATE TABLE cuentas_bancarias (
    id BIGSERIAL PRIMARY KEY,
    persona_id BIGINT NOT NULL REFERENCES personas(id) ON DELETE CASCADE,

    banco VARCHAR(100) NOT NULL,
    numero_cuenta VARCHAR(20),
    clabe VARCHAR(18),
    titular VARCHAR(200),

    es_principal BOOLEAN DEFAULT false,
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cuentas_bancarias_persona ON cuentas_bancarias(persona_id);
CREATE INDEX idx_cuentas_bancarias_clabe ON cuentas_bancarias(clabe);

-- Contratos module

-- Contratos de arrendamiento
CREATE TABLE contratos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    numero_contrato VARCHAR(50) NOT NULL,
    propiedad_id BIGINT NOT NULL REFERENCES propiedades(id),
    arrendatario_id BIGINT NOT NULL REFERENCES personas(id),
    aval_id BIGINT REFERENCES personas(id),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    dia_pago INTEGER NOT NULL,
    monto_renta DECIMAL(12, 2) NOT NULL,
    monto_deposito DECIMAL(12, 2),
    monto_penalidad_diaria DECIMAL(10, 2),
    dias_gracia INTEGER,
    porcentaje_incremento_anual DECIMAL(5, 2),
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    condiciones TEXT,
    notas VARCHAR(500),
    contrato_anterior_id BIGINT REFERENCES contratos(id),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_contratos_empresa_id ON contratos(empresa_id);
CREATE INDEX idx_contratos_propiedad_id ON contratos(propiedad_id);
CREATE INDEX idx_contratos_arrendatario_id ON contratos(arrendatario_id);
CREATE INDEX idx_contratos_aval_id ON contratos(aval_id);
CREATE INDEX idx_contratos_estado ON contratos(estado);
CREATE INDEX idx_contratos_fecha_fin ON contratos(fecha_fin);
CREATE INDEX idx_contratos_numero_contrato ON contratos(numero_contrato);

-- Unique constraint for numero_contrato per empresa
CREATE UNIQUE INDEX idx_contratos_numero_contrato_empresa ON contratos(empresa_id, numero_contrato);

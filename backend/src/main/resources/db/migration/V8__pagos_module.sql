-- Pagos module

-- Cargos (invoices/charges)
CREATE TABLE cargos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    contrato_id BIGINT NOT NULL REFERENCES contratos(id),
    tipo_cargo VARCHAR(30) NOT NULL,
    concepto VARCHAR(200) NOT NULL,
    monto_original DECIMAL(12, 2) NOT NULL,
    monto_pagado DECIMAL(12, 2) NOT NULL DEFAULT 0,
    monto_pendiente DECIMAL(12, 2) NOT NULL,
    fecha_cargo DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    es_cargo_fijo BOOLEAN DEFAULT false,
    periodo_mes INTEGER,
    periodo_anio INTEGER,
    notas VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for cargos
CREATE INDEX idx_cargos_empresa_id ON cargos(empresa_id);
CREATE INDEX idx_cargos_contrato_id ON cargos(contrato_id);
CREATE INDEX idx_cargos_estado ON cargos(estado);
CREATE INDEX idx_cargos_fecha_vencimiento ON cargos(fecha_vencimiento);
CREATE INDEX idx_cargos_tipo_cargo ON cargos(tipo_cargo);
CREATE INDEX idx_cargos_periodo ON cargos(periodo_anio, periodo_mes);

-- Pagos (payments)
CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    contrato_id BIGINT NOT NULL REFERENCES contratos(id),
    persona_id BIGINT NOT NULL REFERENCES personas(id),
    numero_recibo VARCHAR(50),
    monto DECIMAL(12, 2) NOT NULL,
    monto_aplicado DECIMAL(12, 2) DEFAULT 0,
    tipo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago DATE NOT NULL,
    fecha_aplicacion DATE,
    referencia VARCHAR(100),
    banco VARCHAR(100),
    numero_cheque VARCHAR(50),
    notas VARCHAR(500),
    comprobante_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes for pagos
CREATE INDEX idx_pagos_empresa_id ON pagos(empresa_id);
CREATE INDEX idx_pagos_contrato_id ON pagos(contrato_id);
CREATE INDEX idx_pagos_persona_id ON pagos(persona_id);
CREATE INDEX idx_pagos_estado ON pagos(estado);
CREATE INDEX idx_pagos_fecha_pago ON pagos(fecha_pago);
CREATE INDEX idx_pagos_tipo_pago ON pagos(tipo_pago);

-- Pago aplicaciones (payment applications to charges)
CREATE TABLE pago_aplicaciones (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    pago_id BIGINT NOT NULL REFERENCES pagos(id),
    cargo_id BIGINT NOT NULL REFERENCES cargos(id),
    monto_aplicado DECIMAL(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for pago_aplicaciones
CREATE INDEX idx_pago_aplicaciones_empresa_id ON pago_aplicaciones(empresa_id);
CREATE INDEX idx_pago_aplicaciones_pago_id ON pago_aplicaciones(pago_id);
CREATE INDEX idx_pago_aplicaciones_cargo_id ON pago_aplicaciones(cargo_id);

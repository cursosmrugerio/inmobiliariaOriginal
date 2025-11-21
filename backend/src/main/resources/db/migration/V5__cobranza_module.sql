-- V5__cobranza_module.sql
-- Módulo de Control de Cobranza

-- Tabla de cartera vencida
CREATE TABLE cartera_vencida (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    contrato_id BIGINT NOT NULL,
    persona_id BIGINT NOT NULL REFERENCES personas(id),
    propiedad_id BIGINT NOT NULL REFERENCES propiedades(id),
    monto_original DECIMAL(12, 2) NOT NULL,
    monto_pendiente DECIMAL(12, 2) NOT NULL,
    monto_penalidad DECIMAL(12, 2) DEFAULT 0,
    fecha_vencimiento DATE NOT NULL,
    dias_vencido INTEGER DEFAULT 0,
    concepto VARCHAR(500),
    estado_cobranza VARCHAR(50) DEFAULT 'PENDIENTE',
    clasificacion_antiguedad VARCHAR(50) DEFAULT 'VIGENTE',
    porcentaje_penalidad DECIMAL(5, 2) DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cartera_vencida_empresa ON cartera_vencida(empresa_id);
CREATE INDEX idx_cartera_vencida_persona ON cartera_vencida(persona_id);
CREATE INDEX idx_cartera_vencida_propiedad ON cartera_vencida(propiedad_id);
CREATE INDEX idx_cartera_vencida_contrato ON cartera_vencida(contrato_id);
CREATE INDEX idx_cartera_vencida_fecha ON cartera_vencida(fecha_vencimiento);
CREATE INDEX idx_cartera_vencida_estado ON cartera_vencida(estado_cobranza);
CREATE INDEX idx_cartera_vencida_clasificacion ON cartera_vencida(clasificacion_antiguedad);

-- Tabla de seguimiento de cobranza
CREATE TABLE seguimiento_cobranza (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    cartera_vencida_id BIGINT NOT NULL REFERENCES cartera_vencida(id) ON DELETE CASCADE,
    tipo_contacto VARCHAR(50) NOT NULL,
    fecha_contacto TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(1000),
    resultado VARCHAR(50),
    fecha_promesa_pago DATE,
    monto_promesa DECIMAL(12, 2),
    usuario_id BIGINT,
    proxima_accion VARCHAR(500),
    fecha_proxima_accion DATE,
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_seguimiento_empresa ON seguimiento_cobranza(empresa_id);
CREATE INDEX idx_seguimiento_cartera ON seguimiento_cobranza(cartera_vencida_id);
CREATE INDEX idx_seguimiento_fecha ON seguimiento_cobranza(fecha_contacto);
CREATE INDEX idx_seguimiento_proxima ON seguimiento_cobranza(fecha_proxima_accion);

-- Tabla de proyección de cobranza
CREATE TABLE proyeccion_cobranza (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresas(id),
    periodo DATE NOT NULL,
    monto_proyectado DECIMAL(14, 2) NOT NULL,
    monto_cobrado DECIMAL(14, 2) DEFAULT 0,
    monto_pendiente DECIMAL(14, 2) DEFAULT 0,
    cantidad_contratos INTEGER DEFAULT 0,
    cantidad_pagos_esperados INTEGER DEFAULT 0,
    cantidad_pagos_recibidos INTEGER DEFAULT 0,
    porcentaje_cumplimiento DECIMAL(5, 2) DEFAULT 0,
    notas VARCHAR(1000),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_proyeccion_empresa ON proyeccion_cobranza(empresa_id);
CREATE INDEX idx_proyeccion_periodo ON proyeccion_cobranza(periodo);
CREATE UNIQUE INDEX idx_proyeccion_empresa_periodo ON proyeccion_cobranza(empresa_id, periodo);

-- Comentarios de tablas
COMMENT ON TABLE cartera_vencida IS 'Registro de cuentas por cobrar vencidas';
COMMENT ON TABLE seguimiento_cobranza IS 'Historial de gestiones de cobranza';
COMMENT ON TABLE proyeccion_cobranza IS 'Proyección mensual de cobranza esperada';

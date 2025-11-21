-- Módulo de Notificaciones

-- Tabla de notificaciones
CREATE TABLE notificaciones (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    persona_id BIGINT,
    tipo VARCHAR(20) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    destinatario VARCHAR(255) NOT NULL,
    asunto VARCHAR(500) NOT NULL,
    mensaje TEXT NOT NULL,
    referencia_id BIGINT,
    referencia_tipo VARCHAR(50),
    fecha_programada TIMESTAMP,
    fecha_envio TIMESTAMP,
    intentos INTEGER DEFAULT 0,
    error_mensaje TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notificaciones_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- Índices para notificaciones
CREATE INDEX idx_notificaciones_empresa ON notificaciones(empresa_id);
CREATE INDEX idx_notificaciones_persona ON notificaciones(persona_id);
CREATE INDEX idx_notificaciones_estado ON notificaciones(estado);
CREATE INDEX idx_notificaciones_categoria ON notificaciones(categoria);
CREATE INDEX idx_notificaciones_fecha_programada ON notificaciones(fecha_programada);

-- Tabla de configuración de notificaciones
CREATE TABLE configuracion_notificaciones (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    email_habilitado BOOLEAN DEFAULT TRUE,
    whatsapp_habilitado BOOLEAN DEFAULT FALSE,
    dias_anticipacion INTEGER DEFAULT 7,
    frecuencia_recordatorio INTEGER DEFAULT 3,
    max_intentos INTEGER DEFAULT 3,
    plantilla_email TEXT,
    plantilla_whatsapp TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_configuracion_notif_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
    CONSTRAINT uk_configuracion_empresa_categoria UNIQUE (empresa_id, categoria)
);

-- Índices para configuración
CREATE INDEX idx_configuracion_notif_empresa ON configuracion_notificaciones(empresa_id);
CREATE INDEX idx_configuracion_notif_activo ON configuracion_notificaciones(activo);

-- Comentarios
COMMENT ON TABLE notificaciones IS 'Registro de todas las notificaciones enviadas o pendientes';
COMMENT ON TABLE configuracion_notificaciones IS 'Configuración de alertas y recordatorios por empresa';

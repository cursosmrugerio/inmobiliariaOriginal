-- Catálogos Base para Sistema Inmobiliario

-- Catálogo de Estados
CREATE TABLE cat_estados (
    id SERIAL PRIMARY KEY,
    clave VARCHAR(2) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT true
);

-- Catálogo de Municipios
CREATE TABLE cat_municipios (
    id SERIAL PRIMARY KEY,
    estado_id INTEGER NOT NULL REFERENCES cat_estados(id),
    clave VARCHAR(5) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    activo BOOLEAN DEFAULT true,
    UNIQUE(estado_id, clave)
);

-- Catálogo de Tipos de Asentamiento
CREATE TABLE cat_tipos_asentamiento (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activo BOOLEAN DEFAULT true
);

-- Catálogo de Colonias
CREATE TABLE cat_colonias (
    id SERIAL PRIMARY KEY,
    municipio_id INTEGER NOT NULL REFERENCES cat_municipios(id),
    tipo_asentamiento_id INTEGER REFERENCES cat_tipos_asentamiento(id),
    nombre VARCHAR(200) NOT NULL,
    codigo_postal VARCHAR(5),
    activo BOOLEAN DEFAULT true
);

-- Catálogo de Códigos Postales
CREATE TABLE cat_codigos_postales (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(5) NOT NULL,
    colonia_id INTEGER REFERENCES cat_colonias(id),
    municipio_id INTEGER NOT NULL REFERENCES cat_municipios(id),
    activo BOOLEAN DEFAULT true
);

-- Catálogo de Roles
CREATE TABLE cat_roles (
    id SERIAL PRIMARY KEY,
    clave VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    activo BOOLEAN DEFAULT true
);

-- Indexes
CREATE INDEX idx_cat_municipios_estado ON cat_municipios(estado_id);
CREATE INDEX idx_cat_colonias_municipio ON cat_colonias(municipio_id);
CREATE INDEX idx_cat_colonias_tipo ON cat_colonias(tipo_asentamiento_id);
CREATE INDEX idx_cat_codigos_postales_municipio ON cat_codigos_postales(municipio_id);
CREATE INDEX idx_cat_codigos_postales_codigo ON cat_codigos_postales(codigo);

-- Datos iniciales: Estados de México
INSERT INTO cat_estados (clave, nombre) VALUES
('01', 'Aguascalientes'),
('02', 'Baja California'),
('03', 'Baja California Sur'),
('04', 'Campeche'),
('05', 'Coahuila'),
('06', 'Colima'),
('07', 'Chiapas'),
('08', 'Chihuahua'),
('09', 'Ciudad de México'),
('10', 'Durango'),
('11', 'Guanajuato'),
('12', 'Guerrero'),
('13', 'Hidalgo'),
('14', 'Jalisco'),
('15', 'México'),
('16', 'Michoacán'),
('17', 'Morelos'),
('18', 'Nayarit'),
('19', 'Nuevo León'),
('20', 'Oaxaca'),
('21', 'Puebla'),
('22', 'Querétaro'),
('23', 'Quintana Roo'),
('24', 'San Luis Potosí'),
('25', 'Sinaloa'),
('26', 'Sonora'),
('27', 'Tabasco'),
('28', 'Tamaulipas'),
('29', 'Tlaxcala'),
('30', 'Veracruz'),
('31', 'Yucatán'),
('32', 'Zacatecas');

-- Datos iniciales: Tipos de Asentamiento comunes
INSERT INTO cat_tipos_asentamiento (nombre) VALUES
('Colonia'),
('Fraccionamiento'),
('Barrio'),
('Unidad habitacional'),
('Pueblo'),
('Ejido'),
('Ranchería'),
('Condominio'),
('Residencial'),
('Zona industrial'),
('Parque industrial'),
('Ciudad');

-- Datos iniciales: Roles del sistema
INSERT INTO cat_roles (clave, nombre, descripcion) VALUES
('ADMIN', 'Administrador', 'Administrador del sistema con acceso completo'),
('AGENTE', 'Agente Operario', 'Agente con acceso operativo a propiedades y contratos'),
('PROPIETARIO', 'Propietario', 'Propietario de inmuebles'),
('ARRENDATARIO', 'Arrendatario', 'Inquilino o arrendatario'),
('AVAL', 'Aval', 'Aval o fiador de contratos');

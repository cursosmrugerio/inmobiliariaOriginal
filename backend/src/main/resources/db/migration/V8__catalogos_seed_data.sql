-- Seed Data: Municipios, Colonias y Códigos Postales
-- Datos representativos para las principales zonas metropolitanas de México

-- =============================================
-- MUNICIPIOS
-- =============================================

-- Ciudad de México (estado_id = 9)
INSERT INTO cat_municipios (estado_id, clave, nombre) VALUES
(9, '002', 'Azcapotzalco'),
(9, '003', 'Coyoacán'),
(9, '004', 'Cuajimalpa de Morelos'),
(9, '005', 'Gustavo A. Madero'),
(9, '006', 'Iztacalco'),
(9, '007', 'Iztapalapa'),
(9, '008', 'La Magdalena Contreras'),
(9, '009', 'Milpa Alta'),
(9, '010', 'Álvaro Obregón'),
(9, '011', 'Tláhuac'),
(9, '012', 'Tlalpan'),
(9, '013', 'Xochimilco'),
(9, '014', 'Benito Juárez'),
(9, '015', 'Cuauhtémoc'),
(9, '016', 'Miguel Hidalgo'),
(9, '017', 'Venustiano Carranza');

-- Jalisco (estado_id = 14)
INSERT INTO cat_municipios (estado_id, clave, nombre) VALUES
(14, '039', 'Guadalajara'),
(14, '070', 'El Salto'),
(14, '097', 'Tlajomulco de Zúñiga'),
(14, '098', 'Tlaquepaque'),
(14, '120', 'Zapopan'),
(14, '101', 'Tonalá');

-- Nuevo León (estado_id = 19)
INSERT INTO cat_municipios (estado_id, clave, nombre) VALUES
(19, '006', 'Apodaca'),
(19, '018', 'García'),
(19, '019', 'General Escobedo'),
(19, '021', 'Guadalupe'),
(19, '026', 'Juárez'),
(19, '039', 'Monterrey'),
(19, '045', 'San Nicolás de los Garza'),
(19, '046', 'San Pedro Garza García'),
(19, '048', 'Santa Catarina');

-- Estado de México (estado_id = 15)
INSERT INTO cat_municipios (estado_id, clave, nombre) VALUES
(15, '002', 'Acolman'),
(15, '011', 'Atenco'),
(15, '013', 'Atizapán de Zaragoza'),
(15, '020', 'Coacalco de Berriozábal'),
(15, '024', 'Cuautitlán'),
(15, '025', 'Cuautitlán Izcalli'),
(15, '031', 'Chimalhuacán'),
(15, '033', 'Ecatepec de Morelos'),
(15, '037', 'Huixquilucan'),
(15, '039', 'Ixtapaluca'),
(15, '057', 'Naucalpan de Juárez'),
(15, '058', 'Nezahualcóyotl'),
(15, '060', 'Nicolás Romero'),
(15, '081', 'Tecámac'),
(15, '099', 'Texcoco'),
(15, '104', 'Tlalnepantla de Baz'),
(15, '109', 'Tultitlán'),
(15, '122', 'Valle de Chalco Solidaridad');

-- Querétaro (estado_id = 22)
INSERT INTO cat_municipios (estado_id, clave, nombre) VALUES
(22, '006', 'Corregidora'),
(22, '011', 'El Marqués'),
(22, '014', 'Querétaro');

-- =============================================
-- COLONIAS - Ciudad de México
-- =============================================

-- Benito Juárez (municipio Benito Juárez - id depende del orden de inserción)
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Valle Centro', '03100'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Valle Norte', '03103'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Valle Sur', '03104'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Narvarte Poniente', '03020'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Narvarte Oriente', '03023'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Portales Norte', '03300'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Portales Sur', '03303'
FROM cat_municipios m WHERE m.nombre = 'Benito Juárez' AND m.estado_id = 9;

-- Cuauhtémoc
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Roma Norte', '06700'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Roma Sur', '06760'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Condesa', '06140'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Hipódromo Condesa', '06170'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Juárez', '06600'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Centro', '06000'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Santa María la Ribera', '06400'
FROM cat_municipios m WHERE m.nombre = 'Cuauhtémoc' AND m.estado_id = 9;

-- Miguel Hidalgo
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Polanco', '11550'
FROM cat_municipios m WHERE m.nombre = 'Miguel Hidalgo' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Lomas de Chapultepec', '11000'
FROM cat_municipios m WHERE m.nombre = 'Miguel Hidalgo' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Anzures', '11590'
FROM cat_municipios m WHERE m.nombre = 'Miguel Hidalgo' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Tacuba', '11410'
FROM cat_municipios m WHERE m.nombre = 'Miguel Hidalgo' AND m.estado_id = 9;

-- Coyoacán
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Coyoacán Centro', '04000'
FROM cat_municipios m WHERE m.nombre = 'Coyoacán' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Carmen', '04100'
FROM cat_municipios m WHERE m.nombre = 'Coyoacán' AND m.estado_id = 9;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Pedregal de Santo Domingo', '04369'
FROM cat_municipios m WHERE m.nombre = 'Coyoacán' AND m.estado_id = 9;

-- =============================================
-- COLONIAS - Jalisco (Guadalajara)
-- =============================================

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Centro', '44100'
FROM cat_municipios m WHERE m.nombre = 'Guadalajara' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Americana', '44160'
FROM cat_municipios m WHERE m.nombre = 'Guadalajara' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Lafayette', '44150'
FROM cat_municipios m WHERE m.nombre = 'Guadalajara' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Providencia', '44630'
FROM cat_municipios m WHERE m.nombre = 'Guadalajara' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Chapalita', '44500'
FROM cat_municipios m WHERE m.nombre = 'Guadalajara' AND m.estado_id = 14;

-- Zapopan
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Ciudad del Sol', '45050'
FROM cat_municipios m WHERE m.nombre = 'Zapopan' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 2, 'Bugambilias', '45238'
FROM cat_municipios m WHERE m.nombre = 'Zapopan' AND m.estado_id = 14;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 9, 'Puerta de Hierro', '45116'
FROM cat_municipios m WHERE m.nombre = 'Zapopan' AND m.estado_id = 14;

-- =============================================
-- COLONIAS - Nuevo León (Monterrey)
-- =============================================

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Centro', '64000'
FROM cat_municipios m WHERE m.nombre = 'Monterrey' AND m.estado_id = 19;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Valle', '66220'
FROM cat_municipios m WHERE m.nombre = 'Monterrey' AND m.estado_id = 19;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Cumbres', '64610'
FROM cat_municipios m WHERE m.nombre = 'Monterrey' AND m.estado_id = 19;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Contry', '64860'
FROM cat_municipios m WHERE m.nombre = 'Monterrey' AND m.estado_id = 19;

-- San Pedro Garza García
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Del Valle', '66220'
FROM cat_municipios m WHERE m.nombre = 'San Pedro Garza García' AND m.estado_id = 19;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 9, 'Valle de San Ángel', '66260'
FROM cat_municipios m WHERE m.nombre = 'San Pedro Garza García' AND m.estado_id = 19;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Fuentes del Valle', '66220'
FROM cat_municipios m WHERE m.nombre = 'San Pedro Garza García' AND m.estado_id = 19;

-- =============================================
-- COLONIAS - Estado de México
-- =============================================

-- Naucalpan
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Satélite', '53100'
FROM cat_municipios m WHERE m.nombre = 'Naucalpan de Juárez' AND m.estado_id = 15;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 2, 'Lomas Verdes', '53120'
FROM cat_municipios m WHERE m.nombre = 'Naucalpan de Juárez' AND m.estado_id = 15;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Echegaray', '53310'
FROM cat_municipios m WHERE m.nombre = 'Naucalpan de Juárez' AND m.estado_id = 15;

-- Huixquilucan
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 2, 'Interlomas', '52787'
FROM cat_municipios m WHERE m.nombre = 'Huixquilucan' AND m.estado_id = 15;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 9, 'Bosque Real', '52774'
FROM cat_municipios m WHERE m.nombre = 'Huixquilucan' AND m.estado_id = 15;

-- Tlalnepantla
INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Centro', '54000'
FROM cat_municipios m WHERE m.nombre = 'Tlalnepantla de Baz' AND m.estado_id = 15;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 10, 'Tlalnepantla Centro', '54000'
FROM cat_municipios m WHERE m.nombre = 'Tlalnepantla de Baz' AND m.estado_id = 15;

-- =============================================
-- COLONIAS - Querétaro
-- =============================================

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'Centro Histórico', '76000'
FROM cat_municipios m WHERE m.nombre = 'Querétaro' AND m.estado_id = 22;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 2, 'Juriquilla', '76226'
FROM cat_municipios m WHERE m.nombre = 'Querétaro' AND m.estado_id = 22;

INSERT INTO cat_colonias (municipio_id, tipo_asentamiento_id, nombre, codigo_postal)
SELECT m.id, 1, 'El Pueblito', '76900'
FROM cat_municipios m WHERE m.nombre = 'Corregidora' AND m.estado_id = 22;

-- =============================================
-- CÓDIGOS POSTALES
-- =============================================

-- Insert códigos postales based on colonias
INSERT INTO cat_codigos_postales (codigo, colonia_id, municipio_id)
SELECT c.codigo_postal, c.id, c.municipio_id
FROM cat_colonias c
WHERE c.codigo_postal IS NOT NULL
  AND c.codigo_postal != '';

-- Comments
COMMENT ON TABLE cat_municipios IS 'Mexican municipalities with state reference';
COMMENT ON TABLE cat_colonias IS 'Neighborhoods/colonies within municipalities';
COMMENT ON TABLE cat_codigos_postales IS 'Postal codes linked to colonies and municipalities';

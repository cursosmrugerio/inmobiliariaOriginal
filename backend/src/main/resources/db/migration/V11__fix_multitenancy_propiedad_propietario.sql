-- V11: Add empresa_id to propiedad_propietario for multi-tenancy compliance

-- Add empresa_id column (nullable initially for existing data)
ALTER TABLE propiedad_propietario ADD COLUMN IF NOT EXISTS empresa_id BIGINT;

-- Update existing records with empresa_id from related propiedad
UPDATE propiedad_propietario pp
SET empresa_id = p.empresa_id
FROM propiedades p
WHERE pp.propiedad_id = p.id AND pp.empresa_id IS NULL;

-- Make empresa_id NOT NULL after data migration
ALTER TABLE propiedad_propietario ALTER COLUMN empresa_id SET NOT NULL;

-- Add foreign key constraint
ALTER TABLE propiedad_propietario
ADD CONSTRAINT fk_propiedad_propietario_empresa
FOREIGN KEY (empresa_id) REFERENCES empresas(id);

-- Create index for better query performance
CREATE INDEX IF NOT EXISTS idx_propiedad_propietario_empresa_id ON propiedad_propietario(empresa_id);

-- Create composite index for common queries
CREATE INDEX IF NOT EXISTS idx_propiedad_propietario_empresa_propiedad ON propiedad_propietario(empresa_id, propiedad_id);

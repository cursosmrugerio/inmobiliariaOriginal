-- V12: Add monto_fianza column to contratos table

-- Add monto_fianza column (nullable for existing data)
ALTER TABLE contratos ADD COLUMN monto_fianza DECIMAL(12, 2);

-- Add comment for documentation
COMMENT ON COLUMN contratos.monto_fianza IS 'Monto de la fianza (garantía) del arrendatario';

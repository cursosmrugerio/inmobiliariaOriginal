import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  TextField,
  Button,
  Typography,
  Grid,
  MenuItem,
  Alert,
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { useEmpresa } from '../../context/EmpresaContext';
import { pagoService } from '../../services/pagoService';
import { contratoService } from '../../services/contratoService';
import { CreateCargoRequest, TipoCargo } from '../../types/pago';
import { Contrato } from '../../types/contrato';

const tiposCargo: { value: TipoCargo; label: string }[] = [
  { value: 'RENTA', label: 'Renta' },
  { value: 'DEPOSITO', label: 'Depósito' },
  { value: 'PENALIDAD', label: 'Penalidad' },
  { value: 'MANTENIMIENTO', label: 'Mantenimiento' },
  { value: 'SERVICIO', label: 'Servicio' },
  { value: 'OTRO', label: 'Otro' },
];

const CargoForm: React.FC = () => {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [contratos, setContratos] = useState<Contrato[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<CreateCargoRequest>({
    contratoId: 0,
    tipoCargo: 'OTRO',
    concepto: '',
    montoOriginal: 0,
    fechaCargo: new Date().toISOString().split('T')[0],
    fechaVencimiento: new Date().toISOString().split('T')[0],
  });

  useEffect(() => {
    if (empresaActual) {
      loadContratos();
    }
  }, [empresaActual]);

  const loadContratos = async () => {
    try {
      const data = await contratoService.getAll(true);
      setContratos(data);
    } catch (error) {
      console.error('Error loading contratos:', error);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await pagoService.createCargo(formData);
      navigate('/pagos/cargos');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al crear el cargo');
    } finally {
      setLoading(false);
    }
  };

  if (!empresaActual) {
    return <Typography>Seleccione una empresa</Typography>;
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/pagos/cargos')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4">Nuevo Cargo</Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <form onSubmit={handleSubmit}>
        <Paper sx={{ p: 3 }}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <TextField
                select
                fullWidth
                label="Contrato"
                name="contratoId"
                value={formData.contratoId || ''}
                onChange={handleChange}
                required
              >
                <MenuItem value="">Seleccione un contrato</MenuItem>
                {contratos.map((contrato) => (
                  <MenuItem key={contrato.id} value={contrato.id}>
                    {contrato.numeroContrato} - {contrato.arrendatarioNombre}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                select
                fullWidth
                label="Tipo de Cargo"
                name="tipoCargo"
                value={formData.tipoCargo}
                onChange={handleChange}
                required
              >
                {tiposCargo.map((tipo) => (
                  <MenuItem key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Concepto"
                name="concepto"
                value={formData.concepto}
                onChange={handleChange}
                required
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                type="number"
                label="Monto"
                name="montoOriginal"
                value={formData.montoOriginal}
                onChange={handleChange}
                required
                inputProps={{ min: 0.01, step: 0.01 }}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                type="date"
                label="Fecha de Cargo"
                name="fechaCargo"
                value={formData.fechaCargo}
                onChange={handleChange}
                required
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                type="date"
                label="Fecha de Vencimiento"
                name="fechaVencimiento"
                value={formData.fechaVencimiento}
                onChange={handleChange}
                required
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={2}
                label="Notas"
                name="notas"
                value={formData.notas || ''}
                onChange={handleChange}
              />
            </Grid>
          </Grid>
        </Paper>

        <Box display="flex" justifyContent="flex-end" mt={3}>
          <Button
            type="submit"
            variant="contained"
            startIcon={<SaveIcon />}
            disabled={loading || !formData.contratoId || formData.montoOriginal <= 0}
          >
            {loading ? 'Guardando...' : 'Crear Cargo'}
          </Button>
        </Box>
      </form>
    </Box>
  );
};

export default CargoForm;

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
  FormControlLabel,
  Checkbox,
  Alert,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { useEmpresa } from '../../context/EmpresaContext';
import { pagoService } from '../../services/pagoService';
import { contratoService } from '../../services/contratoService';
import { CreatePagoRequest, TipoPago, Cargo } from '../../types/pago';
import { Contrato } from '../../types/contrato';

const tiposPago: { value: TipoPago; label: string }[] = [
  { value: 'EFECTIVO', label: 'Efectivo' },
  { value: 'TRANSFERENCIA', label: 'Transferencia' },
  { value: 'CHEQUE', label: 'Cheque' },
  { value: 'TARJETA_DEBITO', label: 'Tarjeta Débito' },
  { value: 'TARJETA_CREDITO', label: 'Tarjeta Crédito' },
  { value: 'DEPOSITO_BANCARIO', label: 'Depósito Bancario' },
];

const PagoForm: React.FC = () => {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [contratos, setContratos] = useState<Contrato[]>([]);
  const [cargosPendientes, setCargos] = useState<Cargo[]>([]);
  const [selectedCargos, setSelectedCargos] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<CreatePagoRequest>({
    contratoId: 0,
    personaId: 0,
    monto: 0,
    tipoPago: 'TRANSFERENCIA',
    fechaPago: new Date().toISOString().split('T')[0],
    aplicarAutomaticamente: true,
  });

  useEffect(() => {
    if (empresaActual) {
      loadContratos();
    }
  }, [empresaActual]);

  useEffect(() => {
    if (formData.contratoId) {
      loadCargosPendientes(formData.contratoId);
      const contrato = contratos.find((c) => c.id === formData.contratoId);
      if (contrato) {
        setFormData((prev) => ({ ...prev, personaId: contrato.arrendatarioId }));
      }
    }
  }, [formData.contratoId, contratos]);

  const loadContratos = async () => {
    try {
      const data = await contratoService.getAll(true);
      setContratos(data);
    } catch (error) {
      console.error('Error loading contratos:', error);
    }
  };

  const loadCargosPendientes = async (contratoId: number) => {
    try {
      const data = await pagoService.getCargosByContrato(contratoId);
      setCargos(data.filter((c) => c.estado !== 'PAGADO' && c.estado !== 'CANCELADO'));
    } catch (error) {
      console.error('Error loading cargos:', error);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : type === 'number' ? Number(value) : value,
    }));
  };

  const handleCargoToggle = (cargoId: number) => {
    setSelectedCargos((prev) =>
      prev.includes(cargoId) ? prev.filter((id) => id !== cargoId) : [...prev, cargoId]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const request: CreatePagoRequest = {
        ...formData,
        cargoIds: formData.aplicarAutomaticamente ? undefined : selectedCargos,
      };
      await pagoService.create(request);
      navigate('/pagos');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al crear el pago');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
    }).format(value);
  };

  const totalPendiente = cargosPendientes.reduce((sum, c) => sum + c.montoPendiente, 0);

  if (!empresaActual) {
    return <Typography>Seleccione una empresa</Typography>;
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/pagos')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4">Nuevo Pago</Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <form onSubmit={handleSubmit}>
        <Paper sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Información del Pago
          </Typography>
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
                fullWidth
                type="number"
                label="Monto"
                name="monto"
                value={formData.monto}
                onChange={handleChange}
                required
                inputProps={{ min: 0.01, step: 0.01 }}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                select
                fullWidth
                label="Tipo de Pago"
                name="tipoPago"
                value={formData.tipoPago}
                onChange={handleChange}
                required
              >
                {tiposPago.map((tipo) => (
                  <MenuItem key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                type="date"
                label="Fecha de Pago"
                name="fechaPago"
                value={formData.fechaPago}
                onChange={handleChange}
                required
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Referencia"
                name="referencia"
                value={formData.referencia || ''}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                label="Banco"
                name="banco"
                value={formData.banco || ''}
                onChange={handleChange}
              />
            </Grid>
            {formData.tipoPago === 'CHEQUE' && (
              <Grid item xs={12} md={6}>
                <TextField
                  fullWidth
                  label="Número de Cheque"
                  name="numeroCheque"
                  value={formData.numeroCheque || ''}
                  onChange={handleChange}
                />
              </Grid>
            )}
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

        {formData.contratoId > 0 && cargosPendientes.length > 0 && (
          <Paper sx={{ p: 3, mb: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6">Cargos Pendientes</Typography>
              <Typography variant="subtitle1">
                Total: {formatCurrency(totalPendiente)}
              </Typography>
            </Box>

            <FormControlLabel
              control={
                <Checkbox
                  checked={formData.aplicarAutomaticamente}
                  onChange={handleChange}
                  name="aplicarAutomaticamente"
                />
              }
              label="Aplicar automáticamente a cargos más antiguos"
            />

            {!formData.aplicarAutomaticamente && (
              <TableContainer sx={{ mt: 2 }}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell padding="checkbox"></TableCell>
                      <TableCell>Concepto</TableCell>
                      <TableCell>Vencimiento</TableCell>
                      <TableCell align="right">Pendiente</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {cargosPendientes.map((cargo) => (
                      <TableRow key={cargo.id}>
                        <TableCell padding="checkbox">
                          <Checkbox
                            checked={selectedCargos.includes(cargo.id)}
                            onChange={() => handleCargoToggle(cargo.id)}
                          />
                        </TableCell>
                        <TableCell>{cargo.concepto}</TableCell>
                        <TableCell>
                          {new Date(cargo.fechaVencimiento).toLocaleDateString()}
                        </TableCell>
                        <TableCell align="right">
                          {formatCurrency(cargo.montoPendiente)}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            )}
          </Paper>
        )}

        <Box display="flex" justifyContent="flex-end">
          <Button
            type="submit"
            variant="contained"
            startIcon={<SaveIcon />}
            disabled={loading || !formData.contratoId || formData.monto <= 0}
          >
            {loading ? 'Guardando...' : 'Registrar Pago'}
          </Button>
        </Box>
      </form>
    </Box>
  );
};

export default PagoForm;

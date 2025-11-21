import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Typography,
  Chip,
  TextField,
  Grid,
  IconButton,
  Tooltip,
  InputAdornment,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Cancel as CancelIcon,
  ArrowBack as BackIcon,
  AutoMode as AutoIcon,
} from '@mui/icons-material';
import { useEmpresa } from '../../context/EmpresaContext';
import { pagoService } from '../../services/pagoService';
import { Cargo, EstadoCargo, TipoCargo, GenerarCargosFijosRequest } from '../../types/pago';

const estadoColors: Record<EstadoCargo, 'default' | 'primary' | 'success' | 'warning' | 'error'> = {
  PENDIENTE: 'warning',
  PARCIAL: 'primary',
  PAGADO: 'success',
  CANCELADO: 'default',
  VENCIDO: 'error',
};

const tipoCargoLabels: Record<TipoCargo, string> = {
  RENTA: 'Renta',
  DEPOSITO: 'Depósito',
  PENALIDAD: 'Penalidad',
  MANTENIMIENTO: 'Mantenimiento',
  SERVICIO: 'Servicio',
  OTRO: 'Otro',
};

const CargosList: React.FC = () => {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [cargos, setCargos] = useState<Cargo[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<string>('');
  const [dialogOpen, setDialogOpen] = useState(false);
  const [generarRequest, setGenerarRequest] = useState<GenerarCargosFijosRequest>({
    mes: new Date().getMonth() + 1,
    anio: new Date().getFullYear(),
  });

  useEffect(() => {
    if (empresaActual) {
      loadCargos();
    }
  }, [empresaActual]);

  const loadCargos = async () => {
    try {
      setLoading(true);
      const data = await pagoService.getAllCargos();
      setCargos(data);
    } catch (error) {
      console.error('Error loading cargos:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelar = async (id: number) => {
    if (window.confirm('¿Está seguro de cancelar este cargo?')) {
      try {
        await pagoService.cancelarCargo(id);
        loadCargos();
      } catch (error: any) {
        alert(error.response?.data?.message || 'Error al cancelar el cargo');
      }
    }
  };

  const handleGenerarCargos = async () => {
    try {
      await pagoService.generarCargosFijos(generarRequest);
      setDialogOpen(false);
      loadCargos();
    } catch (error) {
      console.error('Error generating cargos:', error);
    }
  };

  const filteredCargos = cargos.filter((cargo) => {
    const matchesSearch =
      cargo.concepto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cargo.arrendatarioNombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      cargo.numeroContrato.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesEstado = !estadoFilter || cargo.estado === estadoFilter;
    return matchesSearch && matchesEstado;
  });

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
    }).format(value);
  };

  if (!empresaActual) {
    return <Typography>Seleccione una empresa</Typography>;
  }

  return (
    <Box>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Box display="flex" alignItems="center">
          <Button startIcon={<BackIcon />} onClick={() => navigate('/pagos')} sx={{ mr: 2 }}>
            Volver
          </Button>
          <Typography variant="h4">Cargos</Typography>
        </Box>
        <Box>
          <Button
            variant="outlined"
            startIcon={<AutoIcon />}
            onClick={() => setDialogOpen(true)}
            sx={{ mr: 1 }}
          >
            Generar Rentas
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/pagos/cargos/nuevo')}
          >
            Nuevo Cargo
          </Button>
        </Box>
      </Box>

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              size="small"
              placeholder="Buscar por concepto, persona o contrato..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <SearchIcon />
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={12} md={3}>
            <FormControl fullWidth size="small">
              <InputLabel>Estado</InputLabel>
              <Select
                value={estadoFilter}
                label="Estado"
                onChange={(e) => setEstadoFilter(e.target.value)}
              >
                <MenuItem value="">Todos</MenuItem>
                <MenuItem value="PENDIENTE">Pendiente</MenuItem>
                <MenuItem value="PARCIAL">Parcial</MenuItem>
                <MenuItem value="PAGADO">Pagado</MenuItem>
                <MenuItem value="VENCIDO">Vencido</MenuItem>
                <MenuItem value="CANCELADO">Cancelado</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Concepto</TableCell>
              <TableCell>Arrendatario</TableCell>
              <TableCell>Contrato</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell>Vencimiento</TableCell>
              <TableCell align="right">Original</TableCell>
              <TableCell align="right">Pendiente</TableCell>
              <TableCell>Estado</TableCell>
              <TableCell>Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={9} align="center">
                  Cargando...
                </TableCell>
              </TableRow>
            ) : filteredCargos.length === 0 ? (
              <TableRow>
                <TableCell colSpan={9} align="center">
                  No se encontraron cargos
                </TableCell>
              </TableRow>
            ) : (
              filteredCargos.map((cargo) => (
                <TableRow key={cargo.id}>
                  <TableCell>{cargo.concepto}</TableCell>
                  <TableCell>{cargo.arrendatarioNombre}</TableCell>
                  <TableCell>{cargo.numeroContrato}</TableCell>
                  <TableCell>{tipoCargoLabels[cargo.tipoCargo]}</TableCell>
                  <TableCell>
                    {new Date(cargo.fechaVencimiento).toLocaleDateString()}
                  </TableCell>
                  <TableCell align="right">{formatCurrency(cargo.montoOriginal)}</TableCell>
                  <TableCell align="right">{formatCurrency(cargo.montoPendiente)}</TableCell>
                  <TableCell>
                    <Chip
                      label={cargo.estado}
                      color={estadoColors[cargo.estado]}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    {cargo.estado === 'PENDIENTE' && cargo.montoPagado === 0 && (
                      <Tooltip title="Cancelar">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleCancelar(cargo.id)}
                        >
                          <CancelIcon />
                        </IconButton>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <Dialog open={dialogOpen} onClose={() => setDialogOpen(false)}>
        <DialogTitle>Generar Cargos de Renta</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={6}>
              <TextField
                select
                fullWidth
                label="Mes"
                value={generarRequest.mes}
                onChange={(e) =>
                  setGenerarRequest((prev) => ({ ...prev, mes: Number(e.target.value) }))
                }
              >
                {Array.from({ length: 12 }, (_, i) => (
                  <MenuItem key={i + 1} value={i + 1}>
                    {new Date(2000, i).toLocaleString('es', { month: 'long' })}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={6}>
              <TextField
                fullWidth
                type="number"
                label="Año"
                value={generarRequest.anio}
                onChange={(e) =>
                  setGenerarRequest((prev) => ({ ...prev, anio: Number(e.target.value) }))
                }
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDialogOpen(false)}>Cancelar</Button>
          <Button variant="contained" onClick={handleGenerarCargos}>
            Generar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default CargosList;

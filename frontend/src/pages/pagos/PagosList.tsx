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
  Card,
  CardContent,
  IconButton,
  Tooltip,
  InputAdornment,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from '@mui/material';
import {
  Add as AddIcon,
  Search as SearchIcon,
  Visibility as ViewIcon,
  Cancel as CancelIcon,
  Receipt as ReceiptIcon,
  AttachMoney as MoneyIcon,
  Warning as WarningIcon,
  TrendingUp as TrendingIcon,
} from '@mui/icons-material';
import { useEmpresa } from '../../context/EmpresaContext';
import { pagoService } from '../../services/pagoService';
import { Pago, EstadoPago, TipoPago, PagoEstadisticas } from '../../types/pago';

const estadoColors: Record<EstadoPago, 'default' | 'primary' | 'success' | 'warning' | 'error'> = {
  PENDIENTE: 'warning',
  APLICADO: 'success',
  PARCIAL: 'primary',
  RECHAZADO: 'error',
  CANCELADO: 'default',
};

const tipoPagoLabels: Record<TipoPago, string> = {
  EFECTIVO: 'Efectivo',
  TRANSFERENCIA: 'Transferencia',
  CHEQUE: 'Cheque',
  TARJETA_DEBITO: 'Tarjeta Débito',
  TARJETA_CREDITO: 'Tarjeta Crédito',
  DEPOSITO_BANCARIO: 'Depósito Bancario',
};

const PagosList: React.FC = () => {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [pagos, setPagos] = useState<Pago[]>([]);
  const [estadisticas, setEstadisticas] = useState<PagoEstadisticas | null>(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [estadoFilter, setEstadoFilter] = useState<string>('');

  useEffect(() => {
    if (empresaActual) {
      loadData();
    }
  }, [empresaActual]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [pagosData, estadisticasData] = await Promise.all([
        pagoService.getAll(),
        pagoService.getEstadisticas(),
      ]);
      setPagos(pagosData);
      setEstadisticas(estadisticasData);
    } catch (error) {
      console.error('Error loading pagos:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelar = async (id: number) => {
    if (window.confirm('¿Está seguro de cancelar este pago?')) {
      try {
        await pagoService.cancelar(id);
        loadData();
      } catch (error) {
        console.error('Error canceling pago:', error);
      }
    }
  };

  const filteredPagos = pagos.filter((pago) => {
    const matchesSearch =
      pago.numeroRecibo.toLowerCase().includes(searchTerm.toLowerCase()) ||
      pago.personaNombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      pago.numeroContrato.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesEstado = !estadoFilter || pago.estado === estadoFilter;
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
        <Typography variant="h4">Pagos</Typography>
        <Box>
          <Button
            variant="outlined"
            startIcon={<ReceiptIcon />}
            onClick={() => navigate('/pagos/cargos')}
            sx={{ mr: 1 }}
          >
            Ver Cargos
          </Button>
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => navigate('/pagos/nuevo')}
          >
            Nuevo Pago
          </Button>
        </Box>
      </Box>

      {estadisticas && (
        <Grid container spacing={2} mb={3}>
          <Grid item xs={12} sm={6} md={2.4}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <MoneyIcon color="success" sx={{ mr: 1 }} />
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      Pagado este mes
                    </Typography>
                    <Typography variant="h6">
                      {formatCurrency(estadisticas.totalPagadoMes)}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <TrendingIcon color="primary" sx={{ mr: 1 }} />
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      Pagos del mes
                    </Typography>
                    <Typography variant="h6">{estadisticas.totalPagosMes}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <WarningIcon color="warning" sx={{ mr: 1 }} />
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      Total pendiente
                    </Typography>
                    <Typography variant="h6">
                      {formatCurrency(estadisticas.totalPendiente)}
                    </Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <ReceiptIcon color="info" sx={{ mr: 1 }} />
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      Cargos pendientes
                    </Typography>
                    <Typography variant="h6">{estadisticas.totalCargosPendientes}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={12} sm={6} md={2.4}>
            <Card>
              <CardContent>
                <Box display="flex" alignItems="center">
                  <WarningIcon color="error" sx={{ mr: 1 }} />
                  <Box>
                    <Typography variant="body2" color="textSecondary">
                      Cargos vencidos
                    </Typography>
                    <Typography variant="h6">{estadisticas.totalCargosVencidos}</Typography>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      <Paper sx={{ p: 2, mb: 2 }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              size="small"
              placeholder="Buscar por recibo, persona o contrato..."
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
                <MenuItem value="APLICADO">Aplicado</MenuItem>
                <MenuItem value="PARCIAL">Parcial</MenuItem>
                <MenuItem value="RECHAZADO">Rechazado</MenuItem>
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
              <TableCell>Recibo</TableCell>
              <TableCell>Fecha</TableCell>
              <TableCell>Persona</TableCell>
              <TableCell>Contrato</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell align="right">Monto</TableCell>
              <TableCell align="right">Aplicado</TableCell>
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
            ) : filteredPagos.length === 0 ? (
              <TableRow>
                <TableCell colSpan={9} align="center">
                  No se encontraron pagos
                </TableCell>
              </TableRow>
            ) : (
              filteredPagos.map((pago) => (
                <TableRow key={pago.id}>
                  <TableCell>{pago.numeroRecibo}</TableCell>
                  <TableCell>{new Date(pago.fechaPago).toLocaleDateString()}</TableCell>
                  <TableCell>{pago.personaNombre}</TableCell>
                  <TableCell>{pago.numeroContrato}</TableCell>
                  <TableCell>{tipoPagoLabels[pago.tipoPago]}</TableCell>
                  <TableCell align="right">{formatCurrency(pago.monto)}</TableCell>
                  <TableCell align="right">{formatCurrency(pago.montoAplicado)}</TableCell>
                  <TableCell>
                    <Chip
                      label={pago.estado}
                      color={estadoColors[pago.estado]}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Tooltip title="Ver detalle">
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/pagos/${pago.id}`)}
                      >
                        <ViewIcon />
                      </IconButton>
                    </Tooltip>
                    {pago.estado !== 'CANCELADO' && pago.estado !== 'APLICADO' && (
                      <Tooltip title="Cancelar">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleCancelar(pago.id)}
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
    </Box>
  );
};

export default PagosList;

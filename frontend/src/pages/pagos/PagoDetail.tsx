import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  Grid,
  Chip,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Divider,
} from '@mui/material';
import { ArrowBack as BackIcon } from '@mui/icons-material';
import { useEmpresa } from '../../context/EmpresaContext';
import { pagoService } from '../../services/pagoService';
import { Pago, EstadoPago, TipoPago } from '../../types/pago';

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

const PagoDetail: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { empresaActual } = useEmpresa();
  const [pago, setPago] = useState<Pago | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (empresaActual && id) {
      loadPago();
    }
  }, [empresaActual, id]);

  const loadPago = async () => {
    try {
      setLoading(true);
      const data = await pagoService.getById(Number(id));
      setPago(data);
    } catch (error) {
      console.error('Error loading pago:', error);
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

  if (!empresaActual) {
    return <Typography>Seleccione una empresa</Typography>;
  }

  if (loading) {
    return <Typography>Cargando...</Typography>;
  }

  if (!pago) {
    return <Typography>Pago no encontrado</Typography>;
  }

  return (
    <Box>
      <Box display="flex" alignItems="center" mb={3}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/pagos')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4">Detalle del Pago</Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Paper sx={{ p: 3 }}>
            <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
              <Typography variant="h6">Recibo: {pago.numeroRecibo}</Typography>
              <Chip label={pago.estado} color={estadoColors[pago.estado]} />
            </Box>
            <Divider sx={{ mb: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="textSecondary">
                  Persona
                </Typography>
                <Typography variant="body1">{pago.personaNombre}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="textSecondary">
                  Contrato
                </Typography>
                <Typography variant="body1">{pago.numeroContrato}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="textSecondary">
                  Propiedad
                </Typography>
                <Typography variant="body1">{pago.propiedadDireccion}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="textSecondary">
                  Tipo de Pago
                </Typography>
                <Typography variant="body1">{tipoPagoLabels[pago.tipoPago]}</Typography>
              </Grid>
              <Grid item xs={12} sm={6}>
                <Typography variant="body2" color="textSecondary">
                  Fecha de Pago
                </Typography>
                <Typography variant="body1">
                  {new Date(pago.fechaPago).toLocaleDateString()}
                </Typography>
              </Grid>
              {pago.fechaAplicacion && (
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="textSecondary">
                    Fecha de Aplicación
                  </Typography>
                  <Typography variant="body1">
                    {new Date(pago.fechaAplicacion).toLocaleDateString()}
                  </Typography>
                </Grid>
              )}
              {pago.referencia && (
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="textSecondary">
                    Referencia
                  </Typography>
                  <Typography variant="body1">{pago.referencia}</Typography>
                </Grid>
              )}
              {pago.banco && (
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="textSecondary">
                    Banco
                  </Typography>
                  <Typography variant="body1">{pago.banco}</Typography>
                </Grid>
              )}
              {pago.numeroCheque && (
                <Grid item xs={12} sm={6}>
                  <Typography variant="body2" color="textSecondary">
                    Número de Cheque
                  </Typography>
                  <Typography variant="body1">{pago.numeroCheque}</Typography>
                </Grid>
              )}
              {pago.notas && (
                <Grid item xs={12}>
                  <Typography variant="body2" color="textSecondary">
                    Notas
                  </Typography>
                  <Typography variant="body1">{pago.notas}</Typography>
                </Grid>
              )}
            </Grid>
          </Paper>
        </Grid>

        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              Resumen
            </Typography>
            <Box display="flex" justifyContent="space-between" mb={1}>
              <Typography>Monto Total:</Typography>
              <Typography fontWeight="bold">{formatCurrency(pago.monto)}</Typography>
            </Box>
            <Box display="flex" justifyContent="space-between" mb={1}>
              <Typography>Monto Aplicado:</Typography>
              <Typography color="success.main">{formatCurrency(pago.montoAplicado)}</Typography>
            </Box>
            <Divider sx={{ my: 1 }} />
            <Box display="flex" justifyContent="space-between">
              <Typography>Disponible:</Typography>
              <Typography color="primary">{formatCurrency(pago.montoDisponible)}</Typography>
            </Box>
          </Paper>
        </Grid>

        {pago.aplicaciones.length > 0 && (
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>
                Aplicaciones
              </Typography>
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Cargo</TableCell>
                      <TableCell align="right">Monto Aplicado</TableCell>
                      <TableCell>Fecha</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {pago.aplicaciones.map((aplicacion) => (
                      <TableRow key={aplicacion.id}>
                        <TableCell>{aplicacion.cargoConcepto}</TableCell>
                        <TableCell align="right">
                          {formatCurrency(aplicacion.montoAplicado)}
                        </TableCell>
                        <TableCell>
                          {new Date(aplicacion.createdAt).toLocaleDateString()}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </Paper>
          </Grid>
        )}
      </Grid>
    </Box>
  );
};

export default PagoDetail;

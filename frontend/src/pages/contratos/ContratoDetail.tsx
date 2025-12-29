import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  Typography,
  Grid,
  Alert,
  CircularProgress,
  Chip,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Edit as EditIcon,
  PlayArrow as ActivateIcon,
  Stop as TerminateIcon,
  Cancel as CancelIcon,
  Autorenew as RenewIcon
} from '@mui/icons-material';
import { contratoService } from '../../services/contratoService';
import { Contrato, EstadoContrato, RenovarContratoRequest } from '../../types/contrato';
import { useEmpresa } from '../../context/EmpresaContext';

const estadoColors: Record<EstadoContrato, 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning'> = {
  BORRADOR: 'default',
  ACTIVO: 'success',
  POR_VENCER: 'warning',
  VENCIDO: 'error',
  RENOVADO: 'info',
  TERMINADO: 'secondary',
  CANCELADO: 'default'
};

const estadoLabels: Record<EstadoContrato, string> = {
  BORRADOR: 'Borrador',
  ACTIVO: 'Activo',
  POR_VENCER: 'Por Vencer',
  VENCIDO: 'Vencido',
  RENOVADO: 'Renovado',
  TERMINADO: 'Terminado',
  CANCELADO: 'Cancelado'
};

export default function ContratoDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { empresaActual } = useEmpresa();

  const [contrato, setContrato] = useState<Contrato | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Dialog states
  const [terminateDialog, setTerminateDialog] = useState(false);
  const [cancelDialog, setCancelDialog] = useState(false);
  const [renewDialog, setRenewDialog] = useState(false);
  const [motivo, setMotivo] = useState('');
  const [renewData, setRenewData] = useState<RenovarContratoRequest>({
    nuevaFechaFin: '',
    aplicarIncrementoAnual: true
  });

  useEffect(() => {
    if (empresaActual && id) {
      loadContrato();
    }
  }, [empresaActual, id]);

  const loadContrato = async () => {
    try {
      setLoading(true);
      const data = await contratoService.getById(parseInt(id!));
      setContrato(data);
      // Set default renewal date (1 year from current end)
      const currentEnd = new Date(data.fechaFin);
      const newEnd = new Date(currentEnd);
      newEnd.setFullYear(newEnd.getFullYear() + 1);
      setRenewData(prev => ({
        ...prev,
        nuevaFechaFin: newEnd.toISOString().split('T')[0]
      }));
      setError(null);
    } catch (err) {
      setError('Error al cargar contrato');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleActivar = async () => {
    try {
      const updated = await contratoService.activar(parseInt(id!));
      setContrato(updated);
    } catch (err) {
      setError('Error al activar contrato');
      console.error(err);
    }
  };

  const handleTerminar = async () => {
    try {
      const updated = await contratoService.terminar(parseInt(id!), motivo);
      setContrato(updated);
      setTerminateDialog(false);
      setMotivo('');
    } catch (err) {
      setError('Error al terminar contrato');
      console.error(err);
    }
  };

  const handleCancelar = async () => {
    try {
      const updated = await contratoService.cancelar(parseInt(id!), motivo);
      setContrato(updated);
      setCancelDialog(false);
      setMotivo('');
    } catch (err) {
      setError('Error al cancelar contrato');
      console.error(err);
    }
  };

  const handleRenovar = async () => {
    try {
      const newContrato = await contratoService.renovar(parseInt(id!), renewData);
      navigate(`/contratos/${newContrato.id}`);
    } catch (err) {
      setError('Error al renovar contrato');
      console.error(err);
    }
  };

  const formatCurrency = (value?: number) => {
    if (!value) return '-';
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value);
  };

  const formatDate = (date?: string) => {
    if (!date) return '-';
    return new Date(date).toLocaleDateString('es-MX');
  };

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa primero</Alert>
      </Box>
    );
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!contrato) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Contrato no encontrado</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Button startIcon={<BackIcon />} onClick={() => navigate('/contratos')}>
            Volver
          </Button>
          <Typography variant="h4">Contrato {contrato.numeroContrato}</Typography>
          <Chip
            label={estadoLabels[contrato.estado]}
            color={estadoColors[contrato.estado]}
          />
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          {contrato.estado === 'BORRADOR' && (
            <Button
              variant="contained"
              color="success"
              startIcon={<ActivateIcon />}
              onClick={handleActivar}
            >
              Activar
            </Button>
          )}
          {['ACTIVO', 'POR_VENCER', 'VENCIDO'].includes(contrato.estado) && (
            <>
              <Button
                variant="outlined"
                color="primary"
                startIcon={<RenewIcon />}
                onClick={() => setRenewDialog(true)}
              >
                Renovar
              </Button>
              <Button
                variant="outlined"
                color="warning"
                startIcon={<TerminateIcon />}
                onClick={() => setTerminateDialog(true)}
              >
                Terminar
              </Button>
            </>
          )}
          {!['TERMINADO', 'CANCELADO', 'RENOVADO'].includes(contrato.estado) && (
            <Button
              variant="outlined"
              color="error"
              startIcon={<CancelIcon />}
              onClick={() => setCancelDialog(true)}
            >
              Cancelar
            </Button>
          )}
          <Button
            variant="contained"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/contratos/${contrato.id}/edit`)}
          >
            Editar
          </Button>
        </Box>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Grid container spacing={3}>
        {/* Información General */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Información General</Typography>
            <Divider sx={{ mb: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Número de Contrato</Typography>
                <Typography>{contrato.numeroContrato}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Día de Pago</Typography>
                <Typography>Día {contrato.diaPago} de cada mes</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Fecha de Inicio</Typography>
                <Typography>{formatDate(contrato.fechaInicio)}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Fecha de Fin</Typography>
                <Typography>{formatDate(contrato.fechaFin)}</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">Días Restantes</Typography>
                <Typography color={contrato.diasRestantes < 30 ? 'error' : 'inherit'}>
                  {contrato.diasRestantes > 0 ? `${contrato.diasRestantes} días` : 'Vencido'}
                </Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Información Financiera */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Información Financiera</Typography>
            <Divider sx={{ mb: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Renta Mensual</Typography>
                <Typography variant="h6" color="primary">{formatCurrency(contrato.montoRenta)}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Depósito</Typography>
                <Typography>{formatCurrency(contrato.montoDeposito)}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Fianza</Typography>
                <Typography>{formatCurrency(contrato.montoFianza)}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Penalidad Diaria</Typography>
                <Typography>{formatCurrency(contrato.montoPenalidadDiaria)}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body2" color="text.secondary">Días de Gracia</Typography>
                <Typography>{contrato.diasGracia || 0} días</Typography>
              </Grid>
              <Grid item xs={12}>
                <Typography variant="body2" color="text.secondary">Incremento Anual</Typography>
                <Typography>{contrato.porcentajeIncrementoAnual || 0}%</Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Propiedad */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Propiedad</Typography>
            <Divider sx={{ mb: 2 }} />
            <Typography variant="subtitle1">{contrato.propiedadNombre}</Typography>
            <Typography variant="body2" color="text.secondary">
              {contrato.propiedadDireccion}
            </Typography>
          </Paper>
        </Grid>

        {/* Arrendatario y Aval */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Arrendatario</Typography>
            <Divider sx={{ mb: 2 }} />
            <Typography variant="subtitle1">{contrato.arrendatarioNombre}</Typography>
            {contrato.arrendatarioEmail && (
              <Typography variant="body2" color="text.secondary">
                {contrato.arrendatarioEmail}
              </Typography>
            )}
            {contrato.arrendatarioTelefono && (
              <Typography variant="body2" color="text.secondary">
                {contrato.arrendatarioTelefono}
              </Typography>
            )}

            {contrato.avalNombre && (
              <>
                <Typography variant="h6" gutterBottom sx={{ mt: 2 }}>Aval</Typography>
                <Divider sx={{ mb: 2 }} />
                <Typography variant="subtitle1">{contrato.avalNombre}</Typography>
                {contrato.avalTelefono && (
                  <Typography variant="body2" color="text.secondary">
                    {contrato.avalTelefono}
                  </Typography>
                )}
              </>
            )}
          </Paper>
        </Grid>

        {/* Condiciones y Notas */}
        {(contrato.condiciones || contrato.notas) && (
          <Grid item xs={12}>
            <Paper sx={{ p: 3 }}>
              {contrato.condiciones && (
                <>
                  <Typography variant="h6" gutterBottom>Condiciones</Typography>
                  <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', mb: 2 }}>
                    {contrato.condiciones}
                  </Typography>
                </>
              )}
              {contrato.notas && (
                <>
                  <Typography variant="h6" gutterBottom>Notas</Typography>
                  <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap' }}>
                    {contrato.notas}
                  </Typography>
                </>
              )}
            </Paper>
          </Grid>
        )}
      </Grid>

      {/* Terminate Dialog */}
      <Dialog open={terminateDialog} onClose={() => setTerminateDialog(false)}>
        <DialogTitle>Terminar Contrato</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Motivo de terminación"
            value={motivo}
            onChange={(e) => setMotivo(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setTerminateDialog(false)}>Cancelar</Button>
          <Button onClick={handleTerminar} color="warning" variant="contained">
            Terminar
          </Button>
        </DialogActions>
      </Dialog>

      {/* Cancel Dialog */}
      <Dialog open={cancelDialog} onClose={() => setCancelDialog(false)}>
        <DialogTitle>Cancelar Contrato</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            multiline
            rows={3}
            label="Motivo de cancelación"
            value={motivo}
            onChange={(e) => setMotivo(e.target.value)}
            sx={{ mt: 2 }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCancelDialog(false)}>Cerrar</Button>
          <Button onClick={handleCancelar} color="error" variant="contained">
            Cancelar Contrato
          </Button>
        </DialogActions>
      </Dialog>

      {/* Renew Dialog */}
      <Dialog open={renewDialog} onClose={() => setRenewDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Renovar Contrato</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                required
                type="date"
                label="Nueva Fecha de Fin"
                value={renewData.nuevaFechaFin}
                onChange={(e) => setRenewData(prev => ({ ...prev, nuevaFechaFin: e.target.value }))}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                type="number"
                label="Nuevo Monto de Renta (opcional)"
                value={renewData.nuevoMontoRenta || ''}
                onChange={(e) => setRenewData(prev => ({
                  ...prev,
                  nuevoMontoRenta: e.target.value ? parseFloat(e.target.value) : undefined
                }))}
                helperText={`Renta actual: ${formatCurrency(contrato.montoRenta)}`}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={2}
                label="Notas"
                value={renewData.notas || ''}
                onChange={(e) => setRenewData(prev => ({ ...prev, notas: e.target.value }))}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRenewDialog(false)}>Cancelar</Button>
          <Button onClick={handleRenovar} color="primary" variant="contained">
            Renovar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

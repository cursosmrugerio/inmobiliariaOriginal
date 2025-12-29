import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  IconButton,
  Button,
  Grid,
  Tabs,
  Tab,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControlLabel,
  Switch,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Alert,
  Tooltip,
} from '@mui/material';
import {
  Send as SendIcon,
  Cancel as CancelIcon,
  Settings as SettingsIcon,
  Refresh as RefreshIcon,
  Email as EmailIcon,
  WhatsApp as WhatsAppIcon,
  NotificationsActive as NotificationsIcon,
} from '@mui/icons-material';
import {
  notificacionService,
  Notificacion,
  ConfiguracionNotificacion,
  EstadoNotificacion,
  CategoriaNotificacion,
  UpdateConfiguracionRequest,
} from '../../services/notificacionService';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div role="tabpanel" hidden={value !== index} {...other}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const NotificacionesDashboard: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([]);
  const [configuraciones, setConfiguraciones] = useState<ConfiguracionNotificacion[]>([]);
  const [, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [configDialogOpen, setConfigDialogOpen] = useState(false);
  const [selectedConfig, setSelectedConfig] = useState<ConfiguracionNotificacion | null>(null);
  const [editConfig, setEditConfig] = useState<UpdateConfiguracionRequest>({
    categoria: 'PAGO_PENDIENTE',
    emailHabilitado: true,
    whatsappHabilitado: false,
    diasAnticipacion: 7,
    frecuenciaRecordatorio: 3,
    maxIntentos: 3,
    activo: true,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [notifs, configs] = await Promise.all([
        notificacionService.getAll(),
        notificacionService.getConfiguraciones(),
      ]);
      setNotificaciones(notifs);
      setConfiguraciones(configs);
      setError(null);
    } catch (err) {
      setError('Error al cargar datos de notificaciones');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnviar = async (id: number) => {
    try {
      await notificacionService.enviar(id);
      loadData();
    } catch (err) {
      setError('Error al enviar notificación');
    }
  };

  const handleCancelar = async (id: number) => {
    try {
      await notificacionService.cancelar(id);
      loadData();
    } catch (err) {
      setError('Error al cancelar notificación');
    }
  };

  const handleOpenConfigDialog = (config?: ConfiguracionNotificacion) => {
    if (config) {
      setSelectedConfig(config);
      setEditConfig({
        categoria: config.categoria,
        emailHabilitado: config.emailHabilitado,
        whatsappHabilitado: config.whatsappHabilitado,
        diasAnticipacion: config.diasAnticipacion,
        frecuenciaRecordatorio: config.frecuenciaRecordatorio,
        maxIntentos: config.maxIntentos,
        plantillaEmail: config.plantillaEmail,
        plantillaWhatsapp: config.plantillaWhatsapp,
        activo: config.activo,
      });
    } else {
      setSelectedConfig(null);
      setEditConfig({
        categoria: 'PAGO_PENDIENTE',
        emailHabilitado: true,
        whatsappHabilitado: false,
        diasAnticipacion: 7,
        frecuenciaRecordatorio: 3,
        maxIntentos: 3,
        activo: true,
      });
    }
    setConfigDialogOpen(true);
  };

  const handleSaveConfig = async () => {
    try {
      await notificacionService.updateConfiguracion(editConfig);
      setConfigDialogOpen(false);
      loadData();
    } catch (err) {
      setError('Error al guardar configuración');
    }
  };

  const getEstadoChip = (estado: EstadoNotificacion) => {
    const colors: Record<EstadoNotificacion, 'default' | 'success' | 'error' | 'warning'> = {
      PENDIENTE: 'warning',
      ENVIADA: 'success',
      FALLIDA: 'error',
      CANCELADA: 'default',
    };
    return <Chip label={estado} color={colors[estado]} size="small" />;
  };

  const getCategoriaLabel = (categoria: CategoriaNotificacion): string => {
    const labels: Record<CategoriaNotificacion, string> = {
      VENCIMIENTO_CONTRATO: 'Vencimiento Contrato',
      PAGO_PENDIENTE: 'Pago Pendiente',
      PAGO_VENCIDO: 'Pago Vencido',
      CONFIRMACION_PAGO: 'Confirmación Pago',
      RECORDATORIO_GENERAL: 'Recordatorio General',
      ALERTA_MOROSIDAD: 'Alerta Morosidad',
    };
    return labels[categoria] || categoria;
  };

  const getTipoIcon = (tipo: string) => {
    switch (tipo) {
      case 'EMAIL':
        return <EmailIcon fontSize="small" />;
      case 'WHATSAPP':
        return <WhatsAppIcon fontSize="small" />;
      default:
        return <NotificationsIcon fontSize="small" />;
    }
  };

  const pendientes = notificaciones.filter(n => n.estado === 'PENDIENTE').length;
  const enviadas = notificaciones.filter(n => n.estado === 'ENVIADA').length;
  const fallidas = notificaciones.filter(n => n.estado === 'FALLIDA').length;

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Notificaciones</Typography>
        <Button startIcon={<RefreshIcon />} onClick={loadData}>
          Actualizar
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="warning.main" variant="h3">{pendientes}</Typography>
              <Typography color="textSecondary">Pendientes</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="success.main" variant="h3">{enviadas}</Typography>
              <Typography color="textSecondary">Enviadas</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={4}>
          <Card>
            <CardContent>
              <Typography color="error.main" variant="h3">{fallidas}</Typography>
              <Typography color="textSecondary">Fallidas</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Paper sx={{ width: '100%' }}>
        <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
          <Tab label="Historial" />
          <Tab label="Configuración" />
        </Tabs>

        <TabPanel value={tabValue} index={0}>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Tipo</TableCell>
                  <TableCell>Categoría</TableCell>
                  <TableCell>Destinatario</TableCell>
                  <TableCell>Asunto</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Fecha</TableCell>
                  <TableCell>Acciones</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {notificaciones.map((notif) => (
                  <TableRow key={notif.id}>
                    <TableCell>{getTipoIcon(notif.tipo)}</TableCell>
                    <TableCell>{getCategoriaLabel(notif.categoria)}</TableCell>
                    <TableCell>{notif.destinatario}</TableCell>
                    <TableCell>{notif.asunto}</TableCell>
                    <TableCell>{getEstadoChip(notif.estado)}</TableCell>
                    <TableCell>
                      {new Date(notif.fechaEnvio || notif.fechaCreacion).toLocaleDateString()}
                    </TableCell>
                    <TableCell>
                      {notif.estado === 'PENDIENTE' && (
                        <>
                          <Tooltip title="Enviar">
                            <IconButton size="small" onClick={() => handleEnviar(notif.id)}>
                              <SendIcon />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Cancelar">
                            <IconButton size="small" onClick={() => handleCancelar(notif.id)}>
                              <CancelIcon />
                            </IconButton>
                          </Tooltip>
                        </>
                      )}
                      {notif.estado === 'FALLIDA' && (
                        <Tooltip title={notif.errorMensaje || 'Error desconocido'}>
                          <IconButton size="small" onClick={() => handleEnviar(notif.id)}>
                            <RefreshIcon />
                          </IconButton>
                        </Tooltip>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
                {notificaciones.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      No hay notificaciones
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          <Box sx={{ mb: 2 }}>
            <Button
              variant="contained"
              startIcon={<SettingsIcon />}
              onClick={() => handleOpenConfigDialog()}
            >
              Nueva Configuración
            </Button>
          </Box>
          <TableContainer>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Categoría</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>WhatsApp</TableCell>
                  <TableCell>Días Anticipación</TableCell>
                  <TableCell>Frecuencia</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Acciones</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {configuraciones.map((config) => (
                  <TableRow key={config.id}>
                    <TableCell>{getCategoriaLabel(config.categoria)}</TableCell>
                    <TableCell>
                      <Chip
                        label={config.emailHabilitado ? 'Sí' : 'No'}
                        color={config.emailHabilitado ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={config.whatsappHabilitado ? 'Sí' : 'No'}
                        color={config.whatsappHabilitado ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{config.diasAnticipacion} días</TableCell>
                    <TableCell>Cada {config.frecuenciaRecordatorio} días</TableCell>
                    <TableCell>
                      <Chip
                        label={config.activo ? 'Activo' : 'Inactivo'}
                        color={config.activo ? 'success' : 'default'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton size="small" onClick={() => handleOpenConfigDialog(config)}>
                        <SettingsIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
                {configuraciones.length === 0 && (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      No hay configuraciones. Cree una nueva para comenzar.
                    </TableCell>
                  </TableRow>
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>
      </Paper>

      {/* Configuration Dialog */}
      <Dialog open={configDialogOpen} onClose={() => setConfigDialogOpen(false)} maxWidth="md" fullWidth>
        <DialogTitle>
          {selectedConfig ? 'Editar Configuración' : 'Nueva Configuración'}
        </DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Categoría</InputLabel>
                <Select
                  value={editConfig.categoria}
                  label="Categoría"
                  onChange={(e) => setEditConfig({ ...editConfig, categoria: e.target.value as CategoriaNotificacion })}
                  disabled={!!selectedConfig}
                >
                  <MenuItem value="VENCIMIENTO_CONTRATO">Vencimiento Contrato</MenuItem>
                  <MenuItem value="PAGO_PENDIENTE">Pago Pendiente</MenuItem>
                  <MenuItem value="PAGO_VENCIDO">Pago Vencido</MenuItem>
                  <MenuItem value="CONFIRMACION_PAGO">Confirmación Pago</MenuItem>
                  <MenuItem value="RECORDATORIO_GENERAL">Recordatorio General</MenuItem>
                  <MenuItem value="ALERTA_MOROSIDAD">Alerta Morosidad</MenuItem>
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={editConfig.emailHabilitado}
                    onChange={(e) => setEditConfig({ ...editConfig, emailHabilitado: e.target.checked })}
                  />
                }
                label="Email Habilitado"
              />
            </Grid>
            <Grid item xs={6}>
              <FormControlLabel
                control={
                  <Switch
                    checked={editConfig.whatsappHabilitado}
                    onChange={(e) => setEditConfig({ ...editConfig, whatsappHabilitado: e.target.checked })}
                  />
                }
                label="WhatsApp Habilitado"
              />
            </Grid>
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Días de Anticipación"
                type="number"
                value={editConfig.diasAnticipacion}
                onChange={(e) => setEditConfig({ ...editConfig, diasAnticipacion: parseInt(e.target.value) })}
              />
            </Grid>
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Frecuencia (días)"
                type="number"
                value={editConfig.frecuenciaRecordatorio}
                onChange={(e) => setEditConfig({ ...editConfig, frecuenciaRecordatorio: parseInt(e.target.value) })}
              />
            </Grid>
            <Grid item xs={4}>
              <TextField
                fullWidth
                label="Max Intentos"
                type="number"
                value={editConfig.maxIntentos}
                onChange={(e) => setEditConfig({ ...editConfig, maxIntentos: parseInt(e.target.value) })}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Plantilla Email"
                multiline
                rows={4}
                value={editConfig.plantillaEmail || ''}
                onChange={(e) => setEditConfig({ ...editConfig, plantillaEmail: e.target.value })}
                helperText="Variables: {{nombre}}, {{monto}}, {{propiedad}}, {{dias_vencido}}"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Plantilla WhatsApp"
                multiline
                rows={3}
                value={editConfig.plantillaWhatsapp || ''}
                onChange={(e) => setEditConfig({ ...editConfig, plantillaWhatsapp: e.target.value })}
              />
            </Grid>
            <Grid item xs={12}>
              <FormControlLabel
                control={
                  <Switch
                    checked={editConfig.activo}
                    onChange={(e) => setEditConfig({ ...editConfig, activo: e.target.checked })}
                  />
                }
                label="Configuración Activa"
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfigDialogOpen(false)}>Cancelar</Button>
          <Button onClick={handleSaveConfig} variant="contained">Guardar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default NotificacionesDashboard;

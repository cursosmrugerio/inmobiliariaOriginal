import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Button,
  Paper,
  Grid,
  Card,
  CardContent,
  Tabs,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Tooltip,
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Build,
  Person,
  Assignment,
  PlayArrow,
  CheckCircle,
  Cancel,
} from '@mui/icons-material';
import mantenimientoService, {
  Proveedor,
  OrdenMantenimiento,
  CreateProveedorRequest,
  CreateOrdenRequest,
  CategoriaMantenimiento,
  PrioridadOrden,
  EstadoOrden,
  EstadisticasMantenimiento,
  SeguimientoOrden,
} from '../../services/mantenimientoService';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div role="tabpanel" hidden={value !== index} {...other}>
      {value === index && <Box sx={{ py: 3 }}>{children}</Box>}
    </div>
  );
}

const MantenimientoPage: React.FC = () => {
  const [tabValue, setTabValue] = useState(0);
  const [proveedores, setProveedores] = useState<Proveedor[]>([]);
  const [ordenes, setOrdenes] = useState<OrdenMantenimiento[]>([]);
  const [estadisticas, setEstadisticas] = useState<EstadisticasMantenimiento | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Dialogs
  const [proveedorDialog, setProveedorDialog] = useState(false);
  const [ordenDialog, setOrdenDialog] = useState(false);
  const [estadoDialog, setEstadoDialog] = useState(false);
  const [seguimientoDialog, setSeguimientoDialog] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState<{ type: 'proveedor' | 'orden'; id: number } | null>(null);

  // Forms
  const [proveedorForm, setProveedorForm] = useState<CreateProveedorRequest>({
    nombre: '',
    telefonoPrincipal: '',
    email: '',
    categorias: [],
  });
  const [ordenForm, setOrdenForm] = useState<CreateOrdenRequest>({
    propiedadId: 0,
    titulo: '',
    descripcion: '',
    categoria: 'OTROS',
    prioridad: 'MEDIA',
  });
  const [editId, setEditId] = useState<number | null>(null);
  const [estadoOrdenId, setEstadoOrdenId] = useState<number | null>(null);
  const [nuevoEstado, setNuevoEstado] = useState<EstadoOrden>('EN_PROCESO');
  const [comentarioEstado, setComentarioEstado] = useState('');
  const [seguimiento, setSeguimiento] = useState<SeguimientoOrden[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [prov, ord, stats] = await Promise.all([
        mantenimientoService.getAllProveedores(),
        mantenimientoService.getAllOrdenes(),
        mantenimientoService.getEstadisticas(),
      ]);
      setProveedores(prov);
      setOrdenes(ord);
      setEstadisticas(stats);
    } catch (err) {
      setError('Error al cargar datos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Proveedor handlers
  const handleSaveProveedor = async () => {
    try {
      if (editId) {
        const updated = await mantenimientoService.updateProveedor(editId, proveedorForm);
        setProveedores(prev => prev.map(p => p.id === editId ? updated : p));
      } else {
        const created = await mantenimientoService.createProveedor(proveedorForm);
        setProveedores(prev => [created, ...prev]);
      }
      setProveedorDialog(false);
      resetProveedorForm();
      loadData();
    } catch (err) {
      setError('Error al guardar proveedor');
    }
  };

  const handleEditProveedor = (proveedor: Proveedor) => {
    setEditId(proveedor.id);
    setProveedorForm({
      nombre: proveedor.nombre,
      razonSocial: proveedor.razonSocial,
      rfc: proveedor.rfc,
      telefonoPrincipal: proveedor.telefonoPrincipal,
      telefonoSecundario: proveedor.telefonoSecundario,
      email: proveedor.email,
      direccion: proveedor.direccion,
      ciudad: proveedor.ciudad,
      estado: proveedor.estado,
      nombreContacto: proveedor.nombreContacto,
      categorias: proveedor.categorias,
      notas: proveedor.notas,
    });
    setProveedorDialog(true);
  };

  const resetProveedorForm = () => {
    setEditId(null);
    setProveedorForm({
      nombre: '',
      telefonoPrincipal: '',
      email: '',
      categorias: [],
    });
  };

  // Orden handlers
  const handleSaveOrden = async () => {
    try {
      if (editId) {
        const updated = await mantenimientoService.updateOrden(editId, ordenForm);
        setOrdenes(prev => prev.map(o => o.id === editId ? updated : o));
      } else {
        const created = await mantenimientoService.createOrden(ordenForm);
        setOrdenes(prev => [created, ...prev]);
      }
      setOrdenDialog(false);
      resetOrdenForm();
      loadData();
    } catch (err) {
      setError('Error al guardar orden');
    }
  };

  const handleEditOrden = (orden: OrdenMantenimiento) => {
    setEditId(orden.id);
    setOrdenForm({
      propiedadId: orden.propiedadId,
      proveedorId: orden.proveedorId,
      solicitanteId: orden.solicitanteId,
      titulo: orden.titulo,
      descripcion: orden.descripcion,
      categoria: orden.categoria,
      prioridad: orden.prioridad,
      fechaProgramada: orden.fechaProgramada,
      costoEstimado: orden.costoEstimado,
      notasTecnicas: orden.notasTecnicas,
    });
    setOrdenDialog(true);
  };

  const resetOrdenForm = () => {
    setEditId(null);
    setOrdenForm({
      propiedadId: 0,
      titulo: '',
      descripcion: '',
      categoria: 'OTROS',
      prioridad: 'MEDIA',
    });
  };

  const handleCambiarEstado = async () => {
    if (!estadoOrdenId) return;
    try {
      const updated = await mantenimientoService.cambiarEstadoOrden(estadoOrdenId, {
        estado: nuevoEstado,
        comentario: comentarioEstado,
      });
      setOrdenes(prev => prev.map(o => o.id === estadoOrdenId ? updated : o));
      setEstadoDialog(false);
      setEstadoOrdenId(null);
      setComentarioEstado('');
      loadData();
    } catch (err) {
      setError('Error al cambiar estado');
    }
  };

  const handleViewSeguimiento = async (ordenId: number) => {
    try {
      const data = await mantenimientoService.getSeguimientoByOrden(ordenId);
      setSeguimiento(data);
      setSeguimientoDialog(true);
    } catch (err) {
      setError('Error al cargar seguimiento');
    }
  };

  const handleDelete = async () => {
    if (!deleteDialog) return;
    try {
      if (deleteDialog.type === 'proveedor') {
        await mantenimientoService.deleteProveedor(deleteDialog.id);
        setProveedores(prev => prev.filter(p => p.id !== deleteDialog.id));
      } else {
        await mantenimientoService.deleteOrden(deleteDialog.id);
        setOrdenes(prev => prev.filter(o => o.id !== deleteDialog.id));
      }
      setDeleteDialog(null);
      loadData();
    } catch (err) {
      setError('Error al eliminar');
    }
  };

  const categorias: CategoriaMantenimiento[] = [
    'PLOMERIA', 'ELECTRICIDAD', 'CARPINTERIA', 'PINTURA', 'LIMPIEZA',
    'JARDINERIA', 'CERRAJERIA', 'AIRE_ACONDICIONADO', 'OTROS'
  ];

  const prioridades: PrioridadOrden[] = ['BAJA', 'MEDIA', 'ALTA', 'URGENTE'];

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 3 }}>Mantenimiento</Typography>

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

      {/* Stats */}
      <Grid container spacing={2} sx={{ mb: 3 }}>
        <Grid item xs={6} md={2.4}>
          <Card>
            <CardContent>
              <Typography variant="h5">{estadisticas?.proveedoresActivos || 0}</Typography>
              <Typography variant="body2" color="text.secondary">Proveedores</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} md={2.4}>
          <Card>
            <CardContent>
              <Typography variant="h5">{estadisticas?.ordenesPendientes || 0}</Typography>
              <Typography variant="body2" color="text.secondary">Pendientes</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} md={2.4}>
          <Card>
            <CardContent>
              <Typography variant="h5">{estadisticas?.ordenesEnProceso || 0}</Typography>
              <Typography variant="body2" color="text.secondary">En Proceso</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={6} md={2.4}>
          <Card>
            <CardContent>
              <Typography variant="h5">{estadisticas?.ordenesCompletadas || 0}</Typography>
              <Typography variant="body2" color="text.secondary">Completadas</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={2.4}>
          <Card>
            <CardContent>
              <Typography variant="h5">${estadisticas?.costosMesActual?.toFixed(2) || '0.00'}</Typography>
              <Typography variant="body2" color="text.secondary">Costos del Mes</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
          <Tab icon={<Assignment />} label="Ordenes" />
          <Tab icon={<Person />} label="Proveedores" />
        </Tabs>
      </Paper>

      {/* Ordenes Tab */}
      <TabPanel value={tabValue} index={0}>
        <Box sx={{ mb: 2 }}>
          <Button variant="contained" startIcon={<Add />} onClick={() => { resetOrdenForm(); setOrdenDialog(true); }}>
            Nueva Orden
          </Button>
        </Box>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Numero</TableCell>
                <TableCell>Titulo</TableCell>
                <TableCell>Categoria</TableCell>
                <TableCell>Prioridad</TableCell>
                <TableCell>Estado</TableCell>
                <TableCell>Fecha Solicitud</TableCell>
                <TableCell>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {ordenes.map((orden) => (
                <TableRow key={orden.id}>
                  <TableCell>{orden.numeroOrden}</TableCell>
                  <TableCell>{orden.titulo}</TableCell>
                  <TableCell>{mantenimientoService.getCategoriaLabel(orden.categoria)}</TableCell>
                  <TableCell>
                    <Chip
                      label={mantenimientoService.getPrioridadLabel(orden.prioridad)}
                      color={mantenimientoService.getPrioridadColor(orden.prioridad)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={mantenimientoService.getEstadoLabel(orden.estado)}
                      color={mantenimientoService.getEstadoColor(orden.estado)}
                      size="small"
                    />
                  </TableCell>
                  <TableCell>{orden.fechaSolicitud}</TableCell>
                  <TableCell>
                    {orden.estado === 'PENDIENTE' && (
                      <Tooltip title="Iniciar">
                        <IconButton size="small" onClick={() => { setEstadoOrdenId(orden.id); setNuevoEstado('EN_PROCESO'); setEstadoDialog(true); }}>
                          <PlayArrow />
                        </IconButton>
                      </Tooltip>
                    )}
                    {orden.estado === 'EN_PROCESO' && (
                      <Tooltip title="Completar">
                        <IconButton size="small" onClick={() => { setEstadoOrdenId(orden.id); setNuevoEstado('COMPLETADA'); setEstadoDialog(true); }}>
                          <CheckCircle />
                        </IconButton>
                      </Tooltip>
                    )}
                    {(orden.estado === 'PENDIENTE' || orden.estado === 'EN_PROCESO') && (
                      <Tooltip title="Cancelar">
                        <IconButton size="small" onClick={() => { setEstadoOrdenId(orden.id); setNuevoEstado('CANCELADA'); setEstadoDialog(true); }}>
                          <Cancel />
                        </IconButton>
                      </Tooltip>
                    )}
                    <Tooltip title="Ver Seguimiento">
                      <IconButton size="small" onClick={() => handleViewSeguimiento(orden.id)}>
                        <Build />
                      </IconButton>
                    </Tooltip>
                    <IconButton size="small" onClick={() => handleEditOrden(orden)}>
                      <Edit />
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => setDeleteDialog({ type: 'orden', id: orden.id })}>
                      <Delete />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {ordenes.length === 0 && (
                <TableRow>
                  <TableCell colSpan={7} align="center">No hay ordenes de mantenimiento</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </TabPanel>

      {/* Proveedores Tab */}
      <TabPanel value={tabValue} index={1}>
        <Box sx={{ mb: 2 }}>
          <Button variant="contained" startIcon={<Add />} onClick={() => { resetProveedorForm(); setProveedorDialog(true); }}>
            Nuevo Proveedor
          </Button>
        </Box>
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Nombre</TableCell>
                <TableCell>Telefono</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Categorias</TableCell>
                <TableCell>Activo</TableCell>
                <TableCell>Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {proveedores.map((proveedor) => (
                <TableRow key={proveedor.id}>
                  <TableCell>{proveedor.nombre}</TableCell>
                  <TableCell>{proveedor.telefonoPrincipal}</TableCell>
                  <TableCell>{proveedor.email}</TableCell>
                  <TableCell>
                    {proveedor.categorias?.map(cat => (
                      <Chip key={cat} label={mantenimientoService.getCategoriaLabel(cat)} size="small" sx={{ mr: 0.5, mb: 0.5 }} />
                    ))}
                  </TableCell>
                  <TableCell>
                    <Chip label={proveedor.activo ? 'Activo' : 'Inactivo'} color={proveedor.activo ? 'success' : 'default'} size="small" />
                  </TableCell>
                  <TableCell>
                    <IconButton size="small" onClick={() => handleEditProveedor(proveedor)}>
                      <Edit />
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => setDeleteDialog({ type: 'proveedor', id: proveedor.id })}>
                      <Delete />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
              {proveedores.length === 0 && (
                <TableRow>
                  <TableCell colSpan={6} align="center">No hay proveedores registrados</TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </TabPanel>

      {/* Proveedor Dialog */}
      <Dialog open={proveedorDialog} onClose={() => setProveedorDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editId ? 'Editar Proveedor' : 'Nuevo Proveedor'}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField fullWidth label="Nombre" value={proveedorForm.nombre} onChange={e => setProveedorForm(prev => ({ ...prev, nombre: e.target.value }))} required />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Telefono" value={proveedorForm.telefonoPrincipal || ''} onChange={e => setProveedorForm(prev => ({ ...prev, telefonoPrincipal: e.target.value }))} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Email" value={proveedorForm.email || ''} onChange={e => setProveedorForm(prev => ({ ...prev, email: e.target.value }))} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Contacto" value={proveedorForm.nombreContacto || ''} onChange={e => setProveedorForm(prev => ({ ...prev, nombreContacto: e.target.value }))} />
            </Grid>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Categorias</InputLabel>
                <Select
                  multiple
                  value={proveedorForm.categorias || []}
                  onChange={e => setProveedorForm(prev => ({ ...prev, categorias: e.target.value as CategoriaMantenimiento[] }))}
                  label="Categorias"
                >
                  {categorias.map(cat => (
                    <MenuItem key={cat} value={cat}>{mantenimientoService.getCategoriaLabel(cat)}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Notas" multiline rows={2} value={proveedorForm.notas || ''} onChange={e => setProveedorForm(prev => ({ ...prev, notas: e.target.value }))} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setProveedorDialog(false)}>Cancelar</Button>
          <Button variant="contained" onClick={handleSaveProveedor}>Guardar</Button>
        </DialogActions>
      </Dialog>

      {/* Orden Dialog */}
      <Dialog open={ordenDialog} onClose={() => setOrdenDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>{editId ? 'Editar Orden' : 'Nueva Orden'}</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12}>
              <TextField fullWidth label="Titulo" value={ordenForm.titulo} onChange={e => setOrdenForm(prev => ({ ...prev, titulo: e.target.value }))} required />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Descripcion" multiline rows={3} value={ordenForm.descripcion} onChange={e => setOrdenForm(prev => ({ ...prev, descripcion: e.target.value }))} required />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="ID Propiedad" type="number" value={ordenForm.propiedadId || ''} onChange={e => setOrdenForm(prev => ({ ...prev, propiedadId: Number(e.target.value) }))} required />
            </Grid>
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel>Proveedor</InputLabel>
                <Select
                  value={ordenForm.proveedorId || ''}
                  onChange={e => setOrdenForm(prev => ({ ...prev, proveedorId: e.target.value ? Number(e.target.value) : undefined }))}
                  label="Proveedor"
                >
                  <MenuItem value="">Sin asignar</MenuItem>
                  {proveedores.filter(p => p.activo).map(p => (
                    <MenuItem key={p.id} value={p.id}>{p.nombre}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel>Categoria</InputLabel>
                <Select
                  value={ordenForm.categoria}
                  onChange={e => setOrdenForm(prev => ({ ...prev, categoria: e.target.value as CategoriaMantenimiento }))}
                  label="Categoria"
                >
                  {categorias.map(cat => (
                    <MenuItem key={cat} value={cat}>{mantenimientoService.getCategoriaLabel(cat)}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <FormControl fullWidth>
                <InputLabel>Prioridad</InputLabel>
                <Select
                  value={ordenForm.prioridad}
                  onChange={e => setOrdenForm(prev => ({ ...prev, prioridad: e.target.value as PrioridadOrden }))}
                  label="Prioridad"
                >
                  {prioridades.map(pri => (
                    <MenuItem key={pri} value={pri}>{mantenimientoService.getPrioridadLabel(pri)}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Fecha Programada" type="date" InputLabelProps={{ shrink: true }} value={ordenForm.fechaProgramada || ''} onChange={e => setOrdenForm(prev => ({ ...prev, fechaProgramada: e.target.value }))} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Costo Estimado" type="number" value={ordenForm.costoEstimado || ''} onChange={e => setOrdenForm(prev => ({ ...prev, costoEstimado: e.target.value ? Number(e.target.value) : undefined }))} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Notas Tecnicas" multiline rows={2} value={ordenForm.notasTecnicas || ''} onChange={e => setOrdenForm(prev => ({ ...prev, notasTecnicas: e.target.value }))} />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOrdenDialog(false)}>Cancelar</Button>
          <Button variant="contained" onClick={handleSaveOrden}>Guardar</Button>
        </DialogActions>
      </Dialog>

      {/* Cambiar Estado Dialog */}
      <Dialog open={estadoDialog} onClose={() => setEstadoDialog(false)}>
        <DialogTitle>Cambiar Estado</DialogTitle>
        <DialogContent>
          <Typography sx={{ mb: 2 }}>
            Cambiar estado a: <strong>{mantenimientoService.getEstadoLabel(nuevoEstado)}</strong>
          </Typography>
          <TextField
            fullWidth
            label="Comentario"
            multiline
            rows={3}
            value={comentarioEstado}
            onChange={e => setComentarioEstado(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEstadoDialog(false)}>Cancelar</Button>
          <Button variant="contained" onClick={handleCambiarEstado}>Confirmar</Button>
        </DialogActions>
      </Dialog>

      {/* Seguimiento Dialog */}
      <Dialog open={seguimientoDialog} onClose={() => setSeguimientoDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Historial de Seguimiento</DialogTitle>
        <DialogContent>
          {seguimiento.length === 0 ? (
            <Typography>No hay registros de seguimiento</Typography>
          ) : (
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Fecha</TableCell>
                  <TableCell>Estado</TableCell>
                  <TableCell>Comentario</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {seguimiento.map(s => (
                  <TableRow key={s.id}>
                    <TableCell>{new Date(s.fechaRegistro).toLocaleString()}</TableCell>
                    <TableCell>
                      <Chip label={mantenimientoService.getEstadoLabel(s.estadoNuevo)} size="small" />
                    </TableCell>
                    <TableCell>{s.comentario || '-'}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setSeguimientoDialog(false)}>Cerrar</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={!!deleteDialog} onClose={() => setDeleteDialog(null)}>
        <DialogTitle>Confirmar Eliminacion</DialogTitle>
        <DialogContent>
          <Typography>
            Esta seguro de eliminar este {deleteDialog?.type === 'proveedor' ? 'proveedor' : 'orden de mantenimiento'}?
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialog(null)}>Cancelar</Button>
          <Button variant="contained" color="error" onClick={handleDelete}>Eliminar</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

export default MantenimientoPage;

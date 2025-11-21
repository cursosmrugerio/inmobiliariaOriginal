import { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Tabs,
  Tab,
  TextField,
  Button,
  Grid,
  Card,
  CardContent,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
  Alert,
  Autocomplete,
  ButtonGroup,
} from '@mui/material';
import {
  Download as DownloadIcon,
  PictureAsPdf as PdfIcon,
  TableChart as ExcelIcon,
  Description as CsvIcon,
} from '@mui/icons-material';
import { reporteService, downloadBlob } from '../../services/reporteService';
import { personaService } from '../../services/personaService';
import type {
  EstadoCuenta,
  AntiguedadSaldos,
  ReporteCarteraVencida,
  ProyeccionCobranzaReporte,
} from '../../types/reporte';
import type { Persona } from '../../types/persona';

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

export default function ReportesPage() {
  const [tabValue, setTabValue] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Estado de cuenta
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [selectedPersona, setSelectedPersona] = useState<Persona | null>(null);
  const [estadoCuenta, setEstadoCuenta] = useState<EstadoCuenta | null>(null);
  const [fechaInicio, setFechaInicio] = useState('');
  const [fechaFin, setFechaFin] = useState('');

  // Antiguedad de saldos
  const [antiguedadSaldos, setAntiguedadSaldos] = useState<AntiguedadSaldos | null>(null);
  const [fechaCorte, setFechaCorte] = useState(new Date().toISOString().split('T')[0]);

  // Cartera vencida
  const [carteraVencida, setCarteraVencida] = useState<ReporteCarteraVencida | null>(null);

  // Proyeccion
  const [proyeccion, setProyeccion] = useState<ProyeccionCobranzaReporte | null>(null);
  const [periodoInicio, setPeriodoInicio] = useState('');
  const [periodoFin, setPeriodoFin] = useState('');

  useEffect(() => {
    loadPersonas();
  }, []);

  const loadPersonas = async () => {
    try {
      const data = await personaService.getAll();
      setPersonas(data);
    } catch {
      console.error('Error loading personas');
    }
  };

  const handleTabChange = (_: React.SyntheticEvent, newValue: number) => {
    setTabValue(newValue);
    setError(null);
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
    }).format(amount);
  };

  // Estado de cuenta handlers
  const handleGenerarEstadoCuenta = async () => {
    if (!selectedPersona) {
      setError('Seleccione un cliente');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const data = await reporteService.getEstadoCuenta(
        selectedPersona.id,
        fechaInicio || undefined,
        fechaFin || undefined
      );
      setEstadoCuenta(data);
    } catch {
      setError('Error al generar estado de cuenta');
    } finally {
      setLoading(false);
    }
  };

  const handleExportEstadoCuenta = async (format: 'excel' | 'csv') => {
    if (!selectedPersona) return;
    try {
      const blob = format === 'excel'
        ? await reporteService.exportEstadoCuentaExcel(selectedPersona.id, fechaInicio || undefined, fechaFin || undefined)
        : await reporteService.exportEstadoCuentaCsv(selectedPersona.id, fechaInicio || undefined, fechaFin || undefined);
      const filename = `estado_cuenta_${selectedPersona.id}.${format === 'excel' ? 'xlsx' : 'csv'}`;
      downloadBlob(blob, filename);
    } catch {
      setError(`Error al exportar a ${format.toUpperCase()}`);
    }
  };

  // Antiguedad de saldos handlers
  const handleGenerarAntiguedadSaldos = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await reporteService.getAntiguedadSaldos(fechaCorte || undefined);
      setAntiguedadSaldos(data);
    } catch {
      setError('Error al generar reporte de antiguedad de saldos');
    } finally {
      setLoading(false);
    }
  };

  const handleExportAntiguedadSaldos = async (format: 'excel' | 'csv') => {
    try {
      const blob = format === 'excel'
        ? await reporteService.exportAntiguedadSaldosExcel(fechaCorte || undefined)
        : await reporteService.exportAntiguedadSaldosCsv(fechaCorte || undefined);
      const filename = `antiguedad_saldos.${format === 'excel' ? 'xlsx' : 'csv'}`;
      downloadBlob(blob, filename);
    } catch {
      setError(`Error al exportar a ${format.toUpperCase()}`);
    }
  };

  // Cartera vencida handlers
  const handleGenerarCarteraVencida = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await reporteService.getCarteraVencida(fechaCorte || undefined);
      setCarteraVencida(data);
    } catch {
      setError('Error al generar reporte de cartera vencida');
    } finally {
      setLoading(false);
    }
  };

  const handleExportCarteraVencida = async (format: 'excel' | 'csv') => {
    try {
      const blob = format === 'excel'
        ? await reporteService.exportCarteraVencidaExcel(fechaCorte || undefined)
        : await reporteService.exportCarteraVencidaCsv(fechaCorte || undefined);
      const filename = `cartera_vencida.${format === 'excel' ? 'xlsx' : 'csv'}`;
      downloadBlob(blob, filename);
    } catch {
      setError(`Error al exportar a ${format.toUpperCase()}`);
    }
  };

  // Proyeccion handlers
  const handleGenerarProyeccion = async () => {
    if (!periodoInicio || !periodoFin) {
      setError('Seleccione el periodo de inicio y fin');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const data = await reporteService.getProyeccion(periodoInicio, periodoFin);
      setProyeccion(data);
    } catch {
      setError('Error al generar reporte de proyeccion');
    } finally {
      setLoading(false);
    }
  };

  const handleExportProyeccion = async (format: 'excel' | 'csv') => {
    if (!periodoInicio || !periodoFin) return;
    try {
      const blob = format === 'excel'
        ? await reporteService.exportProyeccionExcel(periodoInicio, periodoFin)
        : await reporteService.exportProyeccionCsv(periodoInicio, periodoFin);
      const filename = `proyeccion_cobranza.${format === 'excel' ? 'xlsx' : 'csv'}`;
      downloadBlob(blob, filename);
    } catch {
      setError(`Error al exportar a ${format.toUpperCase()}`);
    }
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Reportes
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      <Paper sx={{ width: '100%' }}>
        <Tabs value={tabValue} onChange={handleTabChange}>
          <Tab label="Estado de Cuenta" />
          <Tab label="Antiguedad de Saldos" />
          <Tab label="Cartera Vencida" />
          <Tab label="Proyeccion de Cobranza" />
        </Tabs>

        {/* Estado de Cuenta */}
        <TabPanel value={tabValue} index={0}>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} md={4}>
              <Autocomplete
                options={personas}
                getOptionLabel={(option) => option.nombreCompleto || `${option.nombre} ${option.apellidoPaterno}`}
                value={selectedPersona}
                onChange={(_, newValue) => setSelectedPersona(newValue)}
                renderInput={(params) => (
                  <TextField {...params} label="Cliente" fullWidth />
                )}
              />
            </Grid>
            <Grid item xs={12} md={3}>
              <TextField
                label="Fecha Inicio"
                type="date"
                value={fechaInicio}
                onChange={(e) => setFechaInicio(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={3}>
              <TextField
                label="Fecha Fin"
                type="date"
                value={fechaFin}
                onChange={(e) => setFechaFin(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <Button
                variant="contained"
                onClick={handleGenerarEstadoCuenta}
                disabled={loading}
                fullWidth
                sx={{ height: '56px' }}
              >
                {loading ? <CircularProgress size={24} /> : 'Generar'}
              </Button>
            </Grid>
          </Grid>

          {estadoCuenta && (
            <>
              <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <ButtonGroup variant="outlined">
                  <Button startIcon={<ExcelIcon />} onClick={() => handleExportEstadoCuenta('excel')}>
                    Excel
                  </Button>
                  <Button startIcon={<CsvIcon />} onClick={() => handleExportEstadoCuenta('csv')}>
                    CSV
                  </Button>
                </ButtonGroup>
              </Box>

              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={12} md={6}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">{estadoCuenta.nombreCliente}</Typography>
                      <Typography color="text.secondary">RFC: {estadoCuenta.rfc}</Typography>
                      <Typography color="text.secondary">Email: {estadoCuenta.email}</Typography>
                      <Typography color="text.secondary">Tel: {estadoCuenta.telefono}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Card>
                    <CardContent>
                      <Typography variant="h6">Resumen</Typography>
                      <Typography>Total Cargos: {formatCurrency(estadoCuenta.totalCargos)}</Typography>
                      <Typography>Total Abonos: {formatCurrency(estadoCuenta.totalAbonos)}</Typography>
                      <Typography variant="h6" color="primary">
                        Saldo Actual: {formatCurrency(estadoCuenta.saldoActual)}
                      </Typography>
                      <Typography color="error">
                        Saldo Vencido: {formatCurrency(estadoCuenta.saldoVencido)}
                      </Typography>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <TableContainer component={Paper}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Fecha</TableCell>
                      <TableCell>Concepto</TableCell>
                      <TableCell>Tipo</TableCell>
                      <TableCell align="right">Cargo</TableCell>
                      <TableCell align="right">Abono</TableCell>
                      <TableCell align="right">Saldo</TableCell>
                      <TableCell>Estado</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {estadoCuenta.movimientos.map((mov, index) => (
                      <TableRow key={index}>
                        <TableCell>{mov.fecha}</TableCell>
                        <TableCell>{mov.concepto}</TableCell>
                        <TableCell>{mov.tipo}</TableCell>
                        <TableCell align="right">{formatCurrency(mov.cargo)}</TableCell>
                        <TableCell align="right">{formatCurrency(mov.abono)}</TableCell>
                        <TableCell align="right">{formatCurrency(mov.saldo)}</TableCell>
                        <TableCell>{mov.estado}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </>
          )}
        </TabPanel>

        {/* Antiguedad de Saldos */}
        <TabPanel value={tabValue} index={1}>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} md={4}>
              <TextField
                label="Fecha de Corte"
                type="date"
                value={fechaCorte}
                onChange={(e) => setFechaCorte(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <Button
                variant="contained"
                onClick={handleGenerarAntiguedadSaldos}
                disabled={loading}
                fullWidth
                sx={{ height: '56px' }}
              >
                {loading ? <CircularProgress size={24} /> : 'Generar'}
              </Button>
            </Grid>
          </Grid>

          {antiguedadSaldos && (
            <>
              <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <ButtonGroup variant="outlined">
                  <Button startIcon={<ExcelIcon />} onClick={() => handleExportAntiguedadSaldos('excel')}>
                    Excel
                  </Button>
                  <Button startIcon={<CsvIcon />} onClick={() => handleExportAntiguedadSaldos('csv')}>
                    CSV
                  </Button>
                </ButtonGroup>
              </Box>

              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Vigente</Typography>
                      <Typography variant="h6">{formatCurrency(antiguedadSaldos.totalVigente)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">1-30 dias</Typography>
                      <Typography variant="h6">{formatCurrency(antiguedadSaldos.totalVencido1a30)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">31-60 dias</Typography>
                      <Typography variant="h6">{formatCurrency(antiguedadSaldos.totalVencido31a60)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">61-90 dias</Typography>
                      <Typography variant="h6">{formatCurrency(antiguedadSaldos.totalVencido61a90)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">+90 dias</Typography>
                      <Typography variant="h6" color="error">{formatCurrency(antiguedadSaldos.totalVencidoMas90)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={2}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Total</Typography>
                      <Typography variant="h6" color="primary">{formatCurrency(antiguedadSaldos.totalGeneral)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <TableContainer component={Paper}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Cliente</TableCell>
                      <TableCell align="right">Vigente</TableCell>
                      <TableCell align="right">1-30</TableCell>
                      <TableCell align="right">31-60</TableCell>
                      <TableCell align="right">61-90</TableCell>
                      <TableCell align="right">+90</TableCell>
                      <TableCell align="right">Total</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {antiguedadSaldos.detalle.map((item) => (
                      <TableRow key={item.personaId}>
                        <TableCell>{item.nombreCliente}</TableCell>
                        <TableCell align="right">{formatCurrency(item.vigente)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.vencido1a30)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.vencido31a60)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.vencido61a90)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.vencidoMas90)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.saldoTotal)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </>
          )}
        </TabPanel>

        {/* Cartera Vencida */}
        <TabPanel value={tabValue} index={2}>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} md={4}>
              <TextField
                label="Fecha de Corte"
                type="date"
                value={fechaCorte}
                onChange={(e) => setFechaCorte(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <Button
                variant="contained"
                onClick={handleGenerarCarteraVencida}
                disabled={loading}
                fullWidth
                sx={{ height: '56px' }}
              >
                {loading ? <CircularProgress size={24} /> : 'Generar'}
              </Button>
            </Grid>
          </Grid>

          {carteraVencida && (
            <>
              <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <ButtonGroup variant="outlined">
                  <Button startIcon={<ExcelIcon />} onClick={() => handleExportCarteraVencida('excel')}>
                    Excel
                  </Button>
                  <Button startIcon={<CsvIcon />} onClick={() => handleExportCarteraVencida('csv')}>
                    CSV
                  </Button>
                </ButtonGroup>
              </Box>

              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Total Cartera</Typography>
                      <Typography variant="h6">{formatCurrency(carteraVencida.totalCartera)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Penalidades</Typography>
                      <Typography variant="h6">{formatCurrency(carteraVencida.totalPenalidades)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Total General</Typography>
                      <Typography variant="h6" color="primary">{formatCurrency(carteraVencida.totalGeneral)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Cuentas</Typography>
                      <Typography variant="h6">{carteraVencida.cantidadCuentas}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <TableContainer component={Paper}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Cliente</TableCell>
                      <TableCell>Concepto</TableCell>
                      <TableCell>Vencimiento</TableCell>
                      <TableCell align="right">Dias</TableCell>
                      <TableCell>Clasificacion</TableCell>
                      <TableCell align="right">Pendiente</TableCell>
                      <TableCell align="right">Penalidad</TableCell>
                      <TableCell align="right">Total</TableCell>
                      <TableCell>Estado</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {carteraVencida.detalle.map((item) => (
                      <TableRow key={item.id}>
                        <TableCell>{item.nombreCliente}</TableCell>
                        <TableCell>{item.concepto}</TableCell>
                        <TableCell>{item.fechaVencimiento}</TableCell>
                        <TableCell align="right">{item.diasVencido}</TableCell>
                        <TableCell>{item.clasificacion}</TableCell>
                        <TableCell align="right">{formatCurrency(item.montoPendiente)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.montoPenalidad || 0)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.montoTotal)}</TableCell>
                        <TableCell>{item.estadoCobranza}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </>
          )}
        </TabPanel>

        {/* Proyeccion de Cobranza */}
        <TabPanel value={tabValue} index={3}>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} md={4}>
              <TextField
                label="Periodo Inicio"
                type="date"
                value={periodoInicio}
                onChange={(e) => setPeriodoInicio(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                label="Periodo Fin"
                type="date"
                value={periodoFin}
                onChange={(e) => setPeriodoFin(e.target.value)}
                fullWidth
                InputLabelProps={{ shrink: true }}
              />
            </Grid>
            <Grid item xs={12} md={2}>
              <Button
                variant="contained"
                onClick={handleGenerarProyeccion}
                disabled={loading}
                fullWidth
                sx={{ height: '56px' }}
              >
                {loading ? <CircularProgress size={24} /> : 'Generar'}
              </Button>
            </Grid>
          </Grid>

          {proyeccion && (
            <>
              <Box sx={{ mb: 2, display: 'flex', justifyContent: 'flex-end' }}>
                <ButtonGroup variant="outlined">
                  <Button startIcon={<ExcelIcon />} onClick={() => handleExportProyeccion('excel')}>
                    Excel
                  </Button>
                  <Button startIcon={<CsvIcon />} onClick={() => handleExportProyeccion('csv')}>
                    CSV
                  </Button>
                </ButtonGroup>
              </Box>

              <Grid container spacing={2} sx={{ mb: 3 }}>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Proyectado</Typography>
                      <Typography variant="h6">{formatCurrency(proyeccion.totalProyectado)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Cobrado</Typography>
                      <Typography variant="h6">{formatCurrency(proyeccion.totalCobrado)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Pendiente</Typography>
                      <Typography variant="h6" color="error">{formatCurrency(proyeccion.totalPendiente)}</Typography>
                    </CardContent>
                  </Card>
                </Grid>
                <Grid item xs={6} md={3}>
                  <Card>
                    <CardContent>
                      <Typography variant="subtitle2" color="text.secondary">Cumplimiento</Typography>
                      <Typography variant="h6" color="primary">{proyeccion.porcentajeCumplimiento}%</Typography>
                    </CardContent>
                  </Card>
                </Grid>
              </Grid>

              <TableContainer component={Paper}>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Periodo</TableCell>
                      <TableCell align="right">Proyectado</TableCell>
                      <TableCell align="right">Cobrado</TableCell>
                      <TableCell align="right">Diferencia</TableCell>
                      <TableCell align="right">% Cumplimiento</TableCell>
                      <TableCell align="right">Pagos Esperados</TableCell>
                      <TableCell align="right">Pagos Recibidos</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {proyeccion.detalleMensual.map((item) => (
                      <TableRow key={item.periodo}>
                        <TableCell>{item.mesAnio}</TableCell>
                        <TableCell align="right">{formatCurrency(item.montoProyectado)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.montoCobrado)}</TableCell>
                        <TableCell align="right">{formatCurrency(item.diferencia)}</TableCell>
                        <TableCell align="right">{item.porcentajeCumplimiento}%</TableCell>
                        <TableCell align="right">{item.pagosEsperados}</TableCell>
                        <TableCell align="right">{item.pagosRecibidos}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            </>
          )}
        </TabPanel>
      </Paper>
    </Box>
  );
}

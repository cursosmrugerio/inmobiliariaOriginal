import React, { useEffect, useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Grid,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Button,
  Alert,
  CircularProgress,
  Tabs,
  Tab
} from '@mui/material';
import {
  AccountBalance,
  Warning,
  TrendingUp,
  Assignment
} from '@mui/icons-material';
import { cobranzaService } from '../../services/cobranzaService';
import type {
  CarteraVencida,
  ResumenCobranza,
  SeguimientoCobranza
} from '../../types/cobranza';
import {
  estadoCobranzaLabels,
  clasificacionLabels
} from '../../types/cobranza';

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;
  return (
    <div hidden={value !== index} {...other}>
      {value === index && <Box sx={{ p: 3 }}>{children}</Box>}
    </div>
  );
}

const CobranzaDashboard: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [resumen, setResumen] = useState<ResumenCobranza | null>(null);
  const [carteraVencida, setCarteraVencida] = useState<CarteraVencida[]>([]);
  const [accionesPendientes, setAccionesPendientes] = useState<SeguimientoCobranza[]>([]);
  const [tabValue, setTabValue] = useState(0);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      const [resumenData, carteraData, accionesData] = await Promise.all([
        cobranzaService.getResumenCobranza(),
        cobranzaService.getAllCarteraVencida(),
        cobranzaService.getAccionesPendientes()
      ]);
      setResumen(resumenData);
      setCarteraVencida(carteraData);
      setAccionesPendientes(accionesData);
    } catch (err) {
      setError('Error al cargar los datos de cobranza');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN'
    }).format(amount);
  };

  const getEstadoColor = (estado: string) => {
    const colors: Record<string, 'default' | 'primary' | 'secondary' | 'error' | 'warning' | 'success'> = {
      PENDIENTE: 'warning',
      EN_GESTION: 'primary',
      PROMESA_PAGO: 'secondary',
      PARCIALMENTE_PAGADO: 'default',
      PAGADO: 'success',
      INCOBRABLE: 'error'
    };
    return colors[estado] || 'default';
  };

  const getClasificacionColor = (clasificacion: string) => {
    const colors: Record<string, string> = {
      VIGENTE: '#4caf50',
      VENCIDO_1_30: '#ff9800',
      VENCIDO_31_60: '#f57c00',
      VENCIDO_61_90: '#f44336',
      VENCIDO_MAS_90: '#b71c1c'
    };
    return colors[clasificacion] || '#757575';
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ m: 2 }}>
        {error}
        <Button onClick={loadData} sx={{ ml: 2 }}>
          Reintentar
        </Button>
      </Alert>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Control de Cobranza
      </Typography>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'primary.main', color: 'white' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1}>
                <AccountBalance />
                <Typography variant="subtitle2">Total Cartera Vencida</Typography>
              </Box>
              <Typography variant="h4" sx={{ mt: 1 }}>
                {formatCurrency(resumen?.totalCarteraVencida || 0)}
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.8 }}>
                {resumen?.cantidadCuentasVencidas || 0} cuentas
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'error.main', color: 'white' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1}>
                <Warning />
                <Typography variant="subtitle2">Total Penalidades</Typography>
              </Box>
              <Typography variant="h4" sx={{ mt: 1 }}>
                {formatCurrency(resumen?.totalPenalidades || 0)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'warning.main', color: 'white' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1}>
                <Assignment />
                <Typography variant="subtitle2">En Gestión</Typography>
              </Box>
              <Typography variant="h4" sx={{ mt: 1 }}>
                {resumen?.enGestion || 0}
              </Typography>
              <Typography variant="body2" sx={{ opacity: 0.8 }}>
                {resumen?.promesasPago || 0} promesas de pago
              </Typography>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: 'success.main', color: 'white' }}>
            <CardContent>
              <Box display="flex" alignItems="center" gap={1}>
                <TrendingUp />
                <Typography variant="subtitle2">Total General</Typography>
              </Box>
              <Typography variant="h4" sx={{ mt: 1 }}>
                {formatCurrency(resumen?.totalGeneral || 0)}
              </Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Aging Summary */}
      <Card sx={{ mb: 4 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Antigüedad de Saldos
          </Typography>
          <Grid container spacing={2}>
            {[
              { key: 'VIGENTE', label: 'Vigente', monto: resumen?.montoVigente, cantidad: resumen?.cantidadVigente },
              { key: 'VENCIDO_1_30', label: '1-30 días', monto: resumen?.monto1a30, cantidad: resumen?.cantidad1a30 },
              { key: 'VENCIDO_31_60', label: '31-60 días', monto: resumen?.monto31a60, cantidad: resumen?.cantidad31a60 },
              { key: 'VENCIDO_61_90', label: '61-90 días', monto: resumen?.monto61a90, cantidad: resumen?.cantidad61a90 },
              { key: 'VENCIDO_MAS_90', label: '+90 días', monto: resumen?.montoMas90, cantidad: resumen?.cantidadMas90 }
            ].map((item) => (
              <Grid item xs={12} sm={6} md={2.4} key={item.key}>
                <Box
                  sx={{
                    p: 2,
                    borderRadius: 1,
                    bgcolor: 'grey.100',
                    borderLeft: `4px solid ${getClasificacionColor(item.key)}`
                  }}
                >
                  <Typography variant="subtitle2" color="text.secondary">
                    {item.label}
                  </Typography>
                  <Typography variant="h6">
                    {formatCurrency(item.monto || 0)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    {item.cantidad || 0} cuentas
                  </Typography>
                </Box>
              </Grid>
            ))}
          </Grid>
        </CardContent>
      </Card>

      {/* Tabs for detailed views */}
      <Card>
        <Tabs value={tabValue} onChange={(_, v) => setTabValue(v)}>
          <Tab label="Cartera Vencida" />
          <Tab label="Acciones Pendientes" />
        </Tabs>

        <TabPanel value={tabValue} index={0}>
          <TableContainer component={Paper} variant="outlined">
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Concepto</TableCell>
                  <TableCell>Persona ID</TableCell>
                  <TableCell>Propiedad ID</TableCell>
                  <TableCell align="right">Monto Pendiente</TableCell>
                  <TableCell align="right">Días Vencido</TableCell>
                  <TableCell>Clasificación</TableCell>
                  <TableCell>Estado</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {carteraVencida.length === 0 ? (
                  <TableRow>
                    <TableCell colSpan={7} align="center">
                      No hay cuentas vencidas
                    </TableCell>
                  </TableRow>
                ) : (
                  carteraVencida.map((item) => (
                    <TableRow key={item.id} hover>
                      <TableCell>{item.concepto || '-'}</TableCell>
                      <TableCell>{item.personaId}</TableCell>
                      <TableCell>{item.propiedadId}</TableCell>
                      <TableCell align="right">
                        {formatCurrency(item.montoPendiente)}
                      </TableCell>
                      <TableCell align="right">{item.diasVencido}</TableCell>
                      <TableCell>
                        <Chip
                          label={clasificacionLabels[item.clasificacionAntiguedad]}
                          size="small"
                          sx={{
                            bgcolor: getClasificacionColor(item.clasificacionAntiguedad),
                            color: 'white'
                          }}
                        />
                      </TableCell>
                      <TableCell>
                        <Chip
                          label={estadoCobranzaLabels[item.estadoCobranza]}
                          size="small"
                          color={getEstadoColor(item.estadoCobranza)}
                        />
                      </TableCell>
                    </TableRow>
                  ))
                )}
              </TableBody>
            </Table>
          </TableContainer>
        </TabPanel>

        <TabPanel value={tabValue} index={1}>
          {accionesPendientes.length === 0 ? (
            <Alert severity="info">No hay acciones pendientes</Alert>
          ) : (
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Cartera ID</TableCell>
                    <TableCell>Próxima Acción</TableCell>
                    <TableCell>Fecha</TableCell>
                    <TableCell>Último Contacto</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {accionesPendientes.map((item) => (
                    <TableRow key={item.id} hover>
                      <TableCell>{item.carteraVencidaId}</TableCell>
                      <TableCell>{item.proximaAccion || '-'}</TableCell>
                      <TableCell>{item.fechaProximaAccion || '-'}</TableCell>
                      <TableCell>
                        {new Date(item.fechaContacto).toLocaleDateString('es-MX')}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </TabPanel>
      </Card>
    </Box>
  );
};

export default CobranzaDashboard;

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  IconButton,
  Chip,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Card,
  CardContent,
  Grid
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  Visibility as ViewIcon,
  Description as ContractIcon
} from '@mui/icons-material';
import { contratoService } from '../../services/contratoService';
import { Contrato, EstadoContrato, ContratoStats } from '../../types/contrato';
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

export default function ContratosList() {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [contratos, setContratos] = useState<Contrato[]>([]);
  const [stats, setStats] = useState<ContratoStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
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
      const [contratosData, statsData] = await Promise.all([
        contratoService.getAll(true),
        contratoService.getEstadisticas()
      ]);
      setContratos(contratosData);
      setStats(statsData);
      setError(null);
    } catch (err) {
      setError('Error al cargar contratos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Está seguro de eliminar este contrato?')) return;

    try {
      await contratoService.delete(id);
      setContratos(contratos.filter(c => c.id !== id));
    } catch (err) {
      setError('Error al eliminar contrato');
      console.error(err);
    }
  };

  const filteredContratos = contratos.filter(contrato => {
    const matchesSearch =
      contrato.numeroContrato.toLowerCase().includes(searchTerm.toLowerCase()) ||
      contrato.propiedadNombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      contrato.arrendatarioNombre.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesEstado = !estadoFilter || contrato.estado === estadoFilter;

    return matchesSearch && matchesEstado;
  });

  const formatCurrency = (value?: number) => {
    if (!value) return '-';
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value);
  };

  const formatDate = (date: string) => {
    return new Date(date).toLocaleDateString('es-MX');
  };

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa para ver los contratos</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Contratos</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/contratos/new')}
        >
          Nuevo Contrato
        </Button>
      </Box>

      {/* Stats Cards */}
      {stats && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center', py: 1 }}>
                <Typography variant="h4" color="success.main">{stats.activos}</Typography>
                <Typography variant="body2" color="text.secondary">Activos</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center', py: 1 }}>
                <Typography variant="h4" color="warning.main">{stats.porVencer}</Typography>
                <Typography variant="body2" color="text.secondary">Por Vencer</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center', py: 1 }}>
                <Typography variant="h4" color="error.main">{stats.vencidos}</Typography>
                <Typography variant="body2" color="text.secondary">Vencidos</Typography>
              </CardContent>
            </Card>
          </Grid>
          <Grid item xs={6} sm={3}>
            <Card>
              <CardContent sx={{ textAlign: 'center', py: 1 }}>
                <Typography variant="h4" color="text.secondary">{stats.borradores}</Typography>
                <Typography variant="body2" color="text.secondary">Borradores</Typography>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <TextField
          placeholder="Buscar por número, propiedad o arrendatario..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ flexGrow: 1, minWidth: 250 }}
        />
        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Estado</InputLabel>
          <Select
            value={estadoFilter}
            label="Estado"
            onChange={(e) => setEstadoFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            {Object.entries(estadoLabels).map(([key, label]) => (
              <MenuItem key={key} value={key}>{label}</MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
          <CircularProgress />
        </Box>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>No. Contrato</TableCell>
                <TableCell>Propiedad</TableCell>
                <TableCell>Arrendatario</TableCell>
                <TableCell>Vigencia</TableCell>
                <TableCell align="right">Renta</TableCell>
                <TableCell align="center">Estado</TableCell>
                <TableCell align="center">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredContratos.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} align="center">
                    No se encontraron contratos
                  </TableCell>
                </TableRow>
              ) : (
                filteredContratos.map((contrato) => (
                  <TableRow key={contrato.id}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <ContractIcon color="action" fontSize="small" />
                        {contrato.numeroContrato}
                      </Box>
                    </TableCell>
                    <TableCell>{contrato.propiedadNombre}</TableCell>
                    <TableCell>{contrato.arrendatarioNombre}</TableCell>
                    <TableCell>
                      {formatDate(contrato.fechaInicio)} - {formatDate(contrato.fechaFin)}
                      {contrato.diasRestantes > 0 && (
                        <Typography variant="caption" display="block" color="text.secondary">
                          {contrato.diasRestantes} días restantes
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell align="right">{formatCurrency(contrato.montoRenta)}</TableCell>
                    <TableCell align="center">
                      <Chip
                        label={estadoLabels[contrato.estado]}
                        size="small"
                        color={estadoColors[contrato.estado]}
                      />
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/contratos/${contrato.id}`)}
                        title="Ver detalles"
                      >
                        <ViewIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/contratos/${contrato.id}/edit`)}
                        title="Editar"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(contrato.id)}
                        title="Eliminar"
                        color="error"
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>
      )}
    </Box>
  );
}

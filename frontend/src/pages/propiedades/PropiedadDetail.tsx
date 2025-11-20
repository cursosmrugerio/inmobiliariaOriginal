import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  Typography,
  Grid,
  Chip,
  Alert,
  CircularProgress,
  Divider,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Home as HomeIcon
} from '@mui/icons-material';
import { propiedadService } from '../../services/propiedadService';
import { Propiedad } from '../../types/propiedad';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PropiedadDetail() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { empresaActual } = useEmpresa();
  const [propiedad, setPropiedad] = useState<Propiedad | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id && empresaActual) {
      loadPropiedad(parseInt(id));
    }
  }, [id, empresaActual]);

  const loadPropiedad = async (propiedadId: number) => {
    try {
      setLoading(true);
      const data = await propiedadService.getById(propiedadId);
      setPropiedad(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar la propiedad');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!propiedad || !window.confirm('¿Está seguro de eliminar esta propiedad?')) return;

    try {
      await propiedadService.delete(propiedad.id);
      navigate('/propiedades');
    } catch (err) {
      setError('Error al eliminar la propiedad');
      console.error(err);
    }
  };

  const formatCurrency = (value?: number) => {
    if (!value) return '-';
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value);
  };

  const formatNumber = (value?: number, suffix?: string) => {
    if (!value) return '-';
    return `${value}${suffix || ''}`;
  };

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa</Alert>
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

  if (error || !propiedad) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">{error || 'Propiedad no encontrada'}</Alert>
        <Button sx={{ mt: 2 }} onClick={() => navigate('/propiedades')}>
          Volver a propiedades
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <Button startIcon={<BackIcon />} onClick={() => navigate('/propiedades')}>
            Volver
          </Button>
          <HomeIcon color="action" />
          <Typography variant="h4">{propiedad.nombre}</Typography>
          <Chip
            label={propiedad.disponible ? 'Disponible' : 'No disponible'}
            color={propiedad.disponible ? 'success' : 'default'}
          />
        </Box>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<EditIcon />}
            onClick={() => navigate(`/propiedades/${propiedad.id}/edit`)}
          >
            Editar
          </Button>
          <Button
            variant="outlined"
            color="error"
            startIcon={<DeleteIcon />}
            onClick={handleDelete}
          >
            Eliminar
          </Button>
        </Box>
      </Box>

      <Grid container spacing={3}>
        {/* Información General */}
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Información General</Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Tipo</Typography>
                <Typography variant="body1">
                  <Chip label={propiedad.tipoPropiedadNombre} size="small" color="primary" variant="outlined" />
                </Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Clave Catastral</Typography>
                <Typography variant="body1">{propiedad.claveCatastral || '-'}</Typography>
              </Grid>
              <Grid size={{ xs: 12 }}>
                <Typography variant="body2" color="text.secondary">Dirección</Typography>
                <Typography variant="body1">{propiedad.direccionCompleta || '-'}</Typography>
              </Grid>
              {propiedad.referencias && (
                <Grid size={{ xs: 12 }}>
                  <Typography variant="body2" color="text.secondary">Referencias</Typography>
                  <Typography variant="body1">{propiedad.referencias}</Typography>
                </Grid>
              )}
            </Grid>
          </Paper>
        </Grid>

        {/* Valores */}
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Valores</Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Renta Mensual</Typography>
                <Typography variant="h5" color="primary">{formatCurrency(propiedad.rentaMensual)}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Valor Comercial</Typography>
                <Typography variant="body1">{formatCurrency(propiedad.valorComercial)}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Valor Catastral</Typography>
                <Typography variant="body1">{formatCurrency(propiedad.valorCatastral)}</Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Características */}
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Características</Typography>
            <Grid container spacing={2}>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Superficie Terreno</Typography>
                <Typography variant="body1">{formatNumber(propiedad.superficieTerreno, ' m²')}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Superficie Construcción</Typography>
                <Typography variant="body1">{formatNumber(propiedad.superficieConstruccion, ' m²')}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Recámaras</Typography>
                <Typography variant="body1">{propiedad.numRecamaras || '-'}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Baños</Typography>
                <Typography variant="body1">{propiedad.numBanos || '-'}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Estacionamientos</Typography>
                <Typography variant="body1">{propiedad.numEstacionamientos || '-'}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Pisos</Typography>
                <Typography variant="body1">{propiedad.numPisos || '-'}</Typography>
              </Grid>
              <Grid size={{ xs: 6 }}>
                <Typography variant="body2" color="text.secondary">Año Construcción</Typography>
                <Typography variant="body1">{propiedad.anioConstruccion || '-'}</Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Propietarios */}
        <Grid size={{ xs: 12, md: 6 }}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Propietarios</Typography>
            {propiedad.propietarios && propiedad.propietarios.length > 0 ? (
              <TableContainer>
                <Table size="small">
                  <TableHead>
                    <TableRow>
                      <TableCell>Nombre</TableCell>
                      <TableCell>RFC</TableCell>
                      <TableCell align="right">%</TableCell>
                      <TableCell align="center">Principal</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {propiedad.propietarios.map((pp) => (
                      <TableRow key={pp.id}>
                        <TableCell>{pp.propietarioNombre}</TableCell>
                        <TableCell>{pp.propietarioRfc || '-'}</TableCell>
                        <TableCell align="right">{pp.porcentajePropiedad}%</TableCell>
                        <TableCell align="center">
                          {pp.esPrincipal && <Chip label="Principal" size="small" color="primary" />}
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
            ) : (
              <Typography variant="body2" color="text.secondary">
                No hay propietarios asignados
              </Typography>
            )}
          </Paper>
        </Grid>

        {/* Notas */}
        {propiedad.notas && (
          <Grid size={{ xs: 12 }}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom>Notas</Typography>
              <Typography variant="body1">{propiedad.notas}</Typography>
            </Paper>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}

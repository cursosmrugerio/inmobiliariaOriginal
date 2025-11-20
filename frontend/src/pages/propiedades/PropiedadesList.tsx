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
  CircularProgress
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Search as SearchIcon,
  Visibility as ViewIcon,
  Home as HomeIcon
} from '@mui/icons-material';
import { propiedadService } from '../../services/propiedadService';
import { Propiedad, TipoPropiedad } from '../../types/propiedad';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PropiedadesList() {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [propiedades, setPropiedades] = useState<Propiedad[]>([]);
  const [tipos, setTipos] = useState<TipoPropiedad[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [tipoFilter, setTipoFilter] = useState<string>('');
  const [disponibleFilter, setDisponibleFilter] = useState<string>('');

  useEffect(() => {
    if (empresaActual) {
      loadData();
    }
  }, [empresaActual]);

  const loadData = async () => {
    try {
      setLoading(true);
      const [propiedadesData, tiposData] = await Promise.all([
        propiedadService.getAll(true),
        propiedadService.getTipos()
      ]);
      setPropiedades(propiedadesData);
      setTipos(tiposData);
      setError(null);
    } catch (err) {
      setError('Error al cargar propiedades');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Está seguro de eliminar esta propiedad?')) return;

    try {
      await propiedadService.delete(id);
      setPropiedades(propiedades.filter(p => p.id !== id));
    } catch (err) {
      setError('Error al eliminar propiedad');
      console.error(err);
    }
  };

  const filteredPropiedades = propiedades.filter(propiedad => {
    const matchesSearch = propiedad.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (propiedad.direccionCompleta && propiedad.direccionCompleta.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (propiedad.claveCatastral && propiedad.claveCatastral.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesTipo = !tipoFilter || propiedad.tipoPropiedadId.toString() === tipoFilter;
    const matchesDisponible = !disponibleFilter ||
      (disponibleFilter === 'SI' && propiedad.disponible) ||
      (disponibleFilter === 'NO' && !propiedad.disponible);

    return matchesSearch && matchesTipo && matchesDisponible;
  });

  const formatCurrency = (value?: number) => {
    if (!value) return '-';
    return new Intl.NumberFormat('es-MX', { style: 'currency', currency: 'MXN' }).format(value);
  };

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa para ver las propiedades</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Propiedades</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/propiedades/new')}
        >
          Nueva Propiedad
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <TextField
          placeholder="Buscar por nombre, dirección o clave catastral..."
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
          <InputLabel>Tipo</InputLabel>
          <Select
            value={tipoFilter}
            label="Tipo"
            onChange={(e) => setTipoFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            {tipos.map(tipo => (
              <MenuItem key={tipo.id} value={tipo.id.toString()}>{tipo.nombre}</MenuItem>
            ))}
          </Select>
        </FormControl>
        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Disponible</InputLabel>
          <Select
            value={disponibleFilter}
            label="Disponible"
            onChange={(e) => setDisponibleFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            <MenuItem value="SI">Disponible</MenuItem>
            <MenuItem value="NO">No disponible</MenuItem>
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
                <TableCell>Nombre</TableCell>
                <TableCell>Tipo</TableCell>
                <TableCell>Dirección</TableCell>
                <TableCell align="right">Renta Mensual</TableCell>
                <TableCell align="center">Disponible</TableCell>
                <TableCell align="center">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredPropiedades.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    No se encontraron propiedades
                  </TableCell>
                </TableRow>
              ) : (
                filteredPropiedades.map((propiedad) => (
                  <TableRow key={propiedad.id}>
                    <TableCell>
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <HomeIcon color="action" fontSize="small" />
                        {propiedad.nombre}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={propiedad.tipoPropiedadNombre}
                        size="small"
                        color="primary"
                        variant="outlined"
                      />
                    </TableCell>
                    <TableCell>{propiedad.direccionCompleta || '-'}</TableCell>
                    <TableCell align="right">{formatCurrency(propiedad.rentaMensual)}</TableCell>
                    <TableCell align="center">
                      <Chip
                        label={propiedad.disponible ? 'Sí' : 'No'}
                        size="small"
                        color={propiedad.disponible ? 'success' : 'default'}
                      />
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/propiedades/${propiedad.id}`)}
                        title="Ver detalles"
                      >
                        <ViewIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/propiedades/${propiedad.id}/edit`)}
                        title="Editar"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(propiedad.id)}
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

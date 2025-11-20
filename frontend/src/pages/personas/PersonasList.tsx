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
  Visibility as ViewIcon
} from '@mui/icons-material';
import { personaService } from '../../services/personaService';
import { Persona } from '../../types/persona';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PersonasList() {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const [personas, setPersonas] = useState<Persona[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [tipoFilter, setTipoFilter] = useState<string>('');

  useEffect(() => {
    if (empresaActual) {
      loadPersonas();
    }
  }, [empresaActual]);

  const loadPersonas = async () => {
    try {
      setLoading(true);
      const data = await personaService.getAll(true);
      setPersonas(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar personas');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('¿Está seguro de eliminar esta persona?')) return;

    try {
      await personaService.delete(id);
      setPersonas(personas.filter(p => p.id !== id));
    } catch (err) {
      setError('Error al eliminar persona');
      console.error(err);
    }
  };

  const filteredPersonas = personas.filter(persona => {
    const matchesSearch = persona.nombreCompleto.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (persona.rfc && persona.rfc.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (persona.email && persona.email.toLowerCase().includes(searchTerm.toLowerCase()));

    const matchesTipo = !tipoFilter || persona.tipoPersona === tipoFilter;

    return matchesSearch && matchesTipo;
  });

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa para ver las personas</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Personas</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/personas/new')}
        >
          Nueva Persona
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Box sx={{ display: 'flex', gap: 2, mb: 3 }}>
        <TextField
          placeholder="Buscar por nombre, RFC o email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ flexGrow: 1 }}
        />
        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Tipo</InputLabel>
          <Select
            value={tipoFilter}
            label="Tipo"
            onChange={(e) => setTipoFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            <MenuItem value="FISICA">Física</MenuItem>
            <MenuItem value="MORAL">Moral</MenuItem>
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
                <TableCell>RFC</TableCell>
                <TableCell>Email</TableCell>
                <TableCell>Teléfono</TableCell>
                <TableCell align="center">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredPersonas.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} align="center">
                    No se encontraron personas
                  </TableCell>
                </TableRow>
              ) : (
                filteredPersonas.map((persona) => (
                  <TableRow key={persona.id}>
                    <TableCell>{persona.nombreCompleto}</TableCell>
                    <TableCell>
                      <Chip
                        label={persona.tipoPersona === 'FISICA' ? 'Física' : 'Moral'}
                        size="small"
                        color={persona.tipoPersona === 'FISICA' ? 'primary' : 'secondary'}
                      />
                    </TableCell>
                    <TableCell>{persona.rfc || '-'}</TableCell>
                    <TableCell>{persona.email || '-'}</TableCell>
                    <TableCell>{persona.telefono || '-'}</TableCell>
                    <TableCell align="center">
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/personas/${persona.id}`)}
                        title="Ver detalles"
                      >
                        <ViewIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => navigate(`/personas/${persona.id}/edit`)}
                        title="Editar"
                      >
                        <EditIcon />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => handleDelete(persona.id)}
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

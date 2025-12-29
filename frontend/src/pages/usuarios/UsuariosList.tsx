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
  Tooltip
} from '@mui/material';
import {
  Add as AddIcon,
  Edit as EditIcon,
  Search as SearchIcon,
  Block as BlockIcon,
  CheckCircle as ActiveIcon
} from '@mui/icons-material';
import { usuarioService } from '../../services/usuarioService';
import { Usuario, RolUsuario } from '../../types/usuario';
import { useEmpresa } from '../../context/EmpresaContext';
import { useAuth } from '../../context/AuthContext';

const rolColors: Record<RolUsuario, 'primary' | 'secondary'> = {
  ADMINISTRADOR: 'primary',
  AGENTE: 'secondary'
};

const rolLabels: Record<RolUsuario, string> = {
  ADMINISTRADOR: 'Administrador',
  AGENTE: 'Agente'
};

export default function UsuariosList() {
  const navigate = useNavigate();
  const { empresaActual } = useEmpresa();
  const { user: currentUser } = useAuth();
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [rolFilter, setRolFilter] = useState<string>('');
  const [activoFilter, setActivoFilter] = useState<string>('');

  useEffect(() => {
    if (empresaActual) {
      loadUsuarios();
    }
  }, [empresaActual]);

  const loadUsuarios = async () => {
    try {
      setLoading(true);
      const data = await usuarioService.getAll();
      setUsuarios(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar usuarios');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleActivo = async (id: number) => {
    try {
      const updated = await usuarioService.toggleActivo(id);
      setUsuarios(usuarios.map(u => u.id === id ? updated : u));
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Error al cambiar estado del usuario';
      setError(errorMessage);
      console.error(err);
    }
  };

  const filteredUsuarios = usuarios.filter(usuario => {
    const matchesSearch =
      usuario.nombre.toLowerCase().includes(searchTerm.toLowerCase()) ||
      usuario.apellido.toLowerCase().includes(searchTerm.toLowerCase()) ||
      usuario.email.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesRol = !rolFilter || usuario.rol === rolFilter;
    const matchesActivo = activoFilter === '' || usuario.activo === (activoFilter === 'true');

    return matchesSearch && matchesRol && matchesActivo;
  });

  if (!empresaActual) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="warning">Seleccione una empresa para ver los usuarios</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h4">Usuarios</Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => navigate('/usuarios/nuevo')}
        >
          Nuevo Usuario
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <TextField
          placeholder="Buscar por nombre o email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
          sx={{ flexGrow: 1, minWidth: 200 }}
        />
        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Rol</InputLabel>
          <Select
            value={rolFilter}
            label="Rol"
            onChange={(e) => setRolFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            <MenuItem value="ADMINISTRADOR">Administrador</MenuItem>
            <MenuItem value="AGENTE">Agente</MenuItem>
          </Select>
        </FormControl>
        <FormControl sx={{ minWidth: 150 }}>
          <InputLabel>Estado</InputLabel>
          <Select
            value={activoFilter}
            label="Estado"
            onChange={(e) => setActivoFilter(e.target.value)}
          >
            <MenuItem value="">Todos</MenuItem>
            <MenuItem value="true">Activo</MenuItem>
            <MenuItem value="false">Inactivo</MenuItem>
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
                <TableCell>Email</TableCell>
                <TableCell>Rol</TableCell>
                <TableCell align="center">Estado</TableCell>
                <TableCell align="center">Acciones</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {filteredUsuarios.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={5} align="center">
                    No se encontraron usuarios
                  </TableCell>
                </TableRow>
              ) : (
                filteredUsuarios.map((usuario) => (
                  <TableRow key={usuario.id}>
                    <TableCell>
                      {usuario.nombre} {usuario.apellido}
                      {currentUser?.id === usuario.id && (
                        <Chip label="Yo" size="small" sx={{ ml: 1 }} />
                      )}
                    </TableCell>
                    <TableCell>{usuario.email}</TableCell>
                    <TableCell>
                      <Chip
                        label={rolLabels[usuario.rol]}
                        size="small"
                        color={rolColors[usuario.rol]}
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Chip
                        label={usuario.activo ? 'Activo' : 'Inactivo'}
                        size="small"
                        color={usuario.activo ? 'success' : 'default'}
                      />
                    </TableCell>
                    <TableCell align="center">
                      <Tooltip title="Editar">
                        <IconButton
                          size="small"
                          onClick={() => navigate(`/usuarios/${usuario.id}/editar`)}
                        >
                          <EditIcon />
                        </IconButton>
                      </Tooltip>
                      {currentUser?.id !== usuario.id && (
                        <Tooltip title={usuario.activo ? 'Desactivar' : 'Activar'}>
                          <IconButton
                            size="small"
                            onClick={() => handleToggleActivo(usuario.id)}
                            color={usuario.activo ? 'error' : 'success'}
                          >
                            {usuario.activo ? <BlockIcon /> : <ActiveIcon />}
                          </IconButton>
                        </Tooltip>
                      )}
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

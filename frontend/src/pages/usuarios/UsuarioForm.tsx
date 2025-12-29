import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Alert,
  CircularProgress
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { usuarioService } from '../../services/usuarioService';
import { CreateUsuarioRequest, UpdateUsuarioRequest, RolUsuario, Usuario } from '../../types/usuario';
import { useEmpresa } from '../../context/EmpresaContext';
import { useAuth } from '../../context/AuthContext';

interface FormData {
  email: string;
  password: string;
  nombre: string;
  apellido: string;
  rol: RolUsuario;
}

export default function UsuarioForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { empresaActual } = useEmpresa();
  const { user: currentUser } = useAuth();
  const isEditing = Boolean(id);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<FormData>({
    email: '',
    password: '',
    nombre: '',
    apellido: '',
    rol: 'AGENTE'
  });

  const [originalEmail, setOriginalEmail] = useState<string>('');
  const isCurrentUser = isEditing && originalEmail === currentUser?.email;

  useEffect(() => {
    if (isEditing && id) {
      loadUsuario(parseInt(id));
    }
  }, [id]);

  const loadUsuario = async (usuarioId: number) => {
    try {
      setLoading(true);
      const usuario: Usuario = await usuarioService.getById(usuarioId);
      setFormData({
        email: usuario.email,
        password: '',
        nombre: usuario.nombre,
        apellido: usuario.apellido,
        rol: usuario.rol
      });
      setOriginalEmail(usuario.email);
    } catch (err) {
      setError('Error al cargar usuario');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof FormData, value: string | RolUsuario) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Validations
    if (!formData.email.trim()) {
      setError('El email es requerido');
      return;
    }
    if (!formData.nombre.trim()) {
      setError('El nombre es requerido');
      return;
    }
    if (!formData.apellido.trim()) {
      setError('El apellido es requerido');
      return;
    }
    if (!isEditing && formData.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return;
    }

    try {
      setSaving(true);
      setError(null);

      if (isEditing && id) {
        const updateData: UpdateUsuarioRequest = {
          nombre: formData.nombre,
          apellido: formData.apellido
        };

        // Only include email if changed
        if (formData.email !== originalEmail) {
          updateData.email = formData.email;
        }

        // Only include password if provided
        if (formData.password) {
          updateData.password = formData.password;
        }

        // Only include rol if not current user
        if (!isCurrentUser) {
          updateData.rol = formData.rol;
        }

        await usuarioService.update(parseInt(id), updateData);
      } else {
        const createData: CreateUsuarioRequest = {
          email: formData.email,
          password: formData.password,
          nombre: formData.nombre,
          apellido: formData.apellido,
          rol: formData.rol
        };
        await usuarioService.create(createData);
      }

      navigate('/usuarios');
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Error al guardar usuario';
      setError(errorMessage);
      console.error(err);
    } finally {
      setSaving(false);
    }
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

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/usuarios')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4">
          {isEditing ? 'Editar Usuario' : 'Nuevo Usuario'}
        </Typography>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>{error}</Alert>}

      {isCurrentUser && (
        <Alert severity="info" sx={{ mb: 2 }}>
          Está editando su propio usuario. No puede cambiar su rol.
        </Alert>
      )}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                required
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => handleChange('email', e.target.value)}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                required={!isEditing}
                label={isEditing ? 'Nueva Contraseña (dejar vacío para mantener)' : 'Contraseña'}
                type="password"
                value={formData.password}
                onChange={(e) => handleChange('password', e.target.value)}
                helperText="Mínimo 8 caracteres"
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                required
                label="Nombre"
                value={formData.nombre}
                onChange={(e) => handleChange('nombre', e.target.value)}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <TextField
                fullWidth
                required
                label="Apellido"
                value={formData.apellido}
                onChange={(e) => handleChange('apellido', e.target.value)}
              />
            </Grid>

            <Grid item xs={12} md={6}>
              <FormControl fullWidth disabled={isCurrentUser}>
                <InputLabel>Rol</InputLabel>
                <Select
                  value={formData.rol}
                  label="Rol"
                  onChange={(e) => handleChange('rol', e.target.value as RolUsuario)}
                >
                  <MenuItem value="ADMINISTRADOR">Administrador</MenuItem>
                  <MenuItem value="AGENTE">Agente</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button onClick={() => navigate('/usuarios')}>
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={<SaveIcon />}
                  disabled={saving}
                >
                  {saving ? 'Guardando...' : 'Guardar'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </form>
      </Paper>
    </Box>
  );
}

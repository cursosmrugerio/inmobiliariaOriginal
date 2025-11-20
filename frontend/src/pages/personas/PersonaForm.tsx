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
  CircularProgress,
  Divider
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { personaService } from '../../services/personaService';
import { CreatePersonaRequest, TipoPersona, Persona } from '../../types/persona';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PersonaForm() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { empresaActual } = useEmpresa();
  const isEditing = Boolean(id);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState<CreatePersonaRequest>({
    tipoPersona: 'FISICA',
    nombre: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    fechaNacimiento: '',
    curp: '',
    razonSocial: '',
    nombreComercial: '',
    rfc: '',
    email: '',
    telefono: '',
    telefonoMovil: ''
  });

  useEffect(() => {
    if (isEditing && id) {
      loadPersona(parseInt(id));
    }
  }, [id]);

  const loadPersona = async (personaId: number) => {
    try {
      setLoading(true);
      const persona: Persona = await personaService.getById(personaId);
      setFormData({
        tipoPersona: persona.tipoPersona,
        nombre: persona.nombre || '',
        apellidoPaterno: persona.apellidoPaterno || '',
        apellidoMaterno: persona.apellidoMaterno || '',
        fechaNacimiento: persona.fechaNacimiento || '',
        curp: persona.curp || '',
        razonSocial: persona.razonSocial || '',
        nombreComercial: persona.nombreComercial || '',
        rfc: persona.rfc || '',
        email: persona.email || '',
        telefono: persona.telefono || '',
        telefonoMovil: persona.telefonoMovil || ''
      });
    } catch (err) {
      setError('Error al cargar persona');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreatePersonaRequest, value: string | TipoPersona) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError(null);

      if (isEditing && id) {
        await personaService.update(parseInt(id), formData);
      } else {
        await personaService.create(formData);
      }

      navigate('/personas');
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Error al guardar persona';
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
        <Button startIcon={<BackIcon />} onClick={() => navigate('/personas')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4">
          {isEditing ? 'Editar Persona' : 'Nueva Persona'}
        </Typography>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <FormControl fullWidth>
                <InputLabel>Tipo de Persona</InputLabel>
                <Select
                  value={formData.tipoPersona}
                  label="Tipo de Persona"
                  onChange={(e) => handleChange('tipoPersona', e.target.value as TipoPersona)}
                >
                  <MenuItem value="FISICA">Persona Física</MenuItem>
                  <MenuItem value="MORAL">Persona Moral</MenuItem>
                </Select>
              </FormControl>
            </Grid>

            {formData.tipoPersona === 'FISICA' ? (
              <>
                <Grid item xs={12}>
                  <Divider>Datos de Persona Física</Divider>
                </Grid>
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Nombre"
                    value={formData.nombre}
                    onChange={(e) => handleChange('nombre', e.target.value)}
                    required
                  />
                </Grid>
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Apellido Paterno"
                    value={formData.apellidoPaterno}
                    onChange={(e) => handleChange('apellidoPaterno', e.target.value)}
                    required
                  />
                </Grid>
                <Grid item xs={12} md={4}>
                  <TextField
                    fullWidth
                    label="Apellido Materno"
                    value={formData.apellidoMaterno}
                    onChange={(e) => handleChange('apellidoMaterno', e.target.value)}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Fecha de Nacimiento"
                    type="date"
                    value={formData.fechaNacimiento}
                    onChange={(e) => handleChange('fechaNacimiento', e.target.value)}
                    InputLabelProps={{ shrink: true }}
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="CURP"
                    value={formData.curp}
                    onChange={(e) => handleChange('curp', e.target.value.toUpperCase())}
                    inputProps={{ maxLength: 18 }}
                  />
                </Grid>
              </>
            ) : (
              <>
                <Grid item xs={12}>
                  <Divider>Datos de Persona Moral</Divider>
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Razón Social"
                    value={formData.razonSocial}
                    onChange={(e) => handleChange('razonSocial', e.target.value)}
                    required
                  />
                </Grid>
                <Grid item xs={12} md={6}>
                  <TextField
                    fullWidth
                    label="Nombre Comercial"
                    value={formData.nombreComercial}
                    onChange={(e) => handleChange('nombreComercial', e.target.value)}
                  />
                </Grid>
              </>
            )}

            <Grid item xs={12}>
              <Divider>Datos de Contacto</Divider>
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                label="RFC"
                value={formData.rfc}
                onChange={(e) => handleChange('rfc', e.target.value.toUpperCase())}
                inputProps={{ maxLength: 13 }}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={formData.email}
                onChange={(e) => handleChange('email', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                label="Teléfono"
                value={formData.telefono}
                onChange={(e) => handleChange('telefono', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} md={4}>
              <TextField
                fullWidth
                label="Teléfono Móvil"
                value={formData.telefonoMovil}
                onChange={(e) => handleChange('telefonoMovil', e.target.value)}
              />
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button variant="outlined" onClick={() => navigate('/personas')}>
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

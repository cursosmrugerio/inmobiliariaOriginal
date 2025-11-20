import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  Typography,
  Grid,
  Alert,
  CircularProgress,
  Divider,
  Chip,
  List,
  ListItem,
  ListItemText,
  IconButton,
  Card,
  CardContent,
  CardHeader
} from '@mui/material';
import {
  ArrowBack as BackIcon,
  Edit as EditIcon,
  Delete as DeleteIcon,
  Add as AddIcon,
  Star as StarIcon
} from '@mui/icons-material';
import { personaService } from '../../services/personaService';
import { Persona } from '../../types/persona';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PersonaDetail() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const { empresaActual } = useEmpresa();

  const [persona, setPersona] = useState<Persona | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadPersona(parseInt(id));
    }
  }, [id]);

  const loadPersona = async (personaId: number) => {
    try {
      setLoading(true);
      const data = await personaService.getById(personaId);
      setPersona(data);
      setError(null);
    } catch (err) {
      setError('Error al cargar persona');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteDireccion = async (direccionId: number) => {
    if (!window.confirm('¿Está seguro de eliminar esta dirección?')) return;
    if (!persona) return;

    try {
      await personaService.deleteDireccion(persona.id, direccionId);
      loadPersona(persona.id);
    } catch (err) {
      setError('Error al eliminar dirección');
      console.error(err);
    }
  };

  const handleDeleteCuenta = async (cuentaId: number) => {
    if (!window.confirm('¿Está seguro de eliminar esta cuenta?')) return;
    if (!persona) return;

    try {
      await personaService.deleteCuentaBancaria(persona.id, cuentaId);
      loadPersona(persona.id);
    } catch (err) {
      setError('Error al eliminar cuenta bancaria');
      console.error(err);
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

  if (!persona) {
    return (
      <Box sx={{ p: 3 }}>
        <Alert severity="error">Persona no encontrada</Alert>
      </Box>
    );
  }

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/personas')} sx={{ mr: 2 }}>
          Volver
        </Button>
        <Typography variant="h4" sx={{ flexGrow: 1 }}>
          {persona.nombreCompleto}
        </Typography>
        <Button
          variant="contained"
          startIcon={<EditIcon />}
          onClick={() => navigate(`/personas/${persona.id}/edit`)}
        >
          Editar
        </Button>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Grid container spacing={3}>
        {/* Datos Generales */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Datos Generales</Typography>
            <Divider sx={{ mb: 2 }} />
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">Tipo</Typography>
                <Chip
                  label={persona.tipoPersona === 'FISICA' ? 'Física' : 'Moral'}
                  size="small"
                  color={persona.tipoPersona === 'FISICA' ? 'primary' : 'secondary'}
                />
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">RFC</Typography>
                <Typography>{persona.rfc || '-'}</Typography>
              </Grid>
              {persona.tipoPersona === 'FISICA' ? (
                <>
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" color="text.secondary">CURP</Typography>
                    <Typography>{persona.curp || '-'}</Typography>
                  </Grid>
                  <Grid item xs={12}>
                    <Typography variant="subtitle2" color="text.secondary">Fecha de Nacimiento</Typography>
                    <Typography>{persona.fechaNacimiento || '-'}</Typography>
                  </Grid>
                </>
              ) : (
                <Grid item xs={12}>
                  <Typography variant="subtitle2" color="text.secondary">Nombre Comercial</Typography>
                  <Typography>{persona.nombreComercial || '-'}</Typography>
                </Grid>
              )}
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">Email</Typography>
                <Typography>{persona.email || '-'}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="subtitle2" color="text.secondary">Teléfono</Typography>
                <Typography>{persona.telefono || '-'}</Typography>
              </Grid>
            </Grid>
          </Paper>
        </Grid>

        {/* Roles */}
        <Grid item xs={12} md={6}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>Roles</Typography>
            <Divider sx={{ mb: 2 }} />
            {persona.roles && persona.roles.length > 0 ? (
              <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                {persona.roles.map((rol) => (
                  <Chip key={rol.id} label={rol.rolNombre} color="primary" />
                ))}
              </Box>
            ) : (
              <Typography color="text.secondary">Sin roles asignados</Typography>
            )}
          </Paper>
        </Grid>

        {/* Direcciones */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader
              title="Direcciones"
              action={
                <IconButton onClick={() => {/* TODO: Add address modal */}}>
                  <AddIcon />
                </IconButton>
              }
            />
            <CardContent>
              {persona.direcciones && persona.direcciones.length > 0 ? (
                <List dense>
                  {persona.direcciones.map((direccion) => (
                    <ListItem
                      key={direccion.id}
                      secondaryAction={
                        <IconButton
                          edge="end"
                          size="small"
                          onClick={() => handleDeleteDireccion(direccion.id)}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      }
                    >
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            {direccion.esPrincipal && <StarIcon fontSize="small" color="primary" />}
                            {direccion.calle} {direccion.numeroExterior}
                          </Box>
                        }
                        secondary={`${direccion.coloniaNombre || ''}, ${direccion.municipioNombre || ''}, ${direccion.estadoNombre || ''} ${direccion.codigoPostal || ''}`}
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography color="text.secondary">Sin direcciones registradas</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        {/* Cuentas Bancarias */}
        <Grid item xs={12} md={6}>
          <Card>
            <CardHeader
              title="Cuentas Bancarias"
              action={
                <IconButton onClick={() => {/* TODO: Add bank account modal */}}>
                  <AddIcon />
                </IconButton>
              }
            />
            <CardContent>
              {persona.cuentasBancarias && persona.cuentasBancarias.length > 0 ? (
                <List dense>
                  {persona.cuentasBancarias.map((cuenta) => (
                    <ListItem
                      key={cuenta.id}
                      secondaryAction={
                        <IconButton
                          edge="end"
                          size="small"
                          onClick={() => handleDeleteCuenta(cuenta.id)}
                        >
                          <DeleteIcon fontSize="small" />
                        </IconButton>
                      }
                    >
                      <ListItemText
                        primary={
                          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            {cuenta.esPrincipal && <StarIcon fontSize="small" color="primary" />}
                            {cuenta.banco}
                          </Box>
                        }
                        secondary={`CLABE: ${cuenta.clabe || '-'} | Cuenta: ${cuenta.numeroCuenta || '-'}`}
                      />
                    </ListItem>
                  ))}
                </List>
              ) : (
                <Typography color="text.secondary">Sin cuentas bancarias registradas</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

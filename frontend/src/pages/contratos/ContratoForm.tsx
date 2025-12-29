import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
  Grid,
  Alert,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  InputAdornment
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { contratoService } from '../../services/contratoService';
import { propiedadService } from '../../services/propiedadService';
import { personaService } from '../../services/personaService';
import { CreateContratoRequest, UpdateContratoRequest } from '../../types/contrato';
import { Propiedad } from '../../types/propiedad';
import { Persona } from '../../types/persona';
import { useEmpresa } from '../../context/EmpresaContext';

export default function ContratoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEditing = Boolean(id);
  const { empresaActual } = useEmpresa();

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [propiedades, setPropiedades] = useState<Propiedad[]>([]);
  const [personas, setPersonas] = useState<Persona[]>([]);

  const [formData, setFormData] = useState<CreateContratoRequest>({
    numeroContrato: '',
    propiedadId: 0,
    arrendatarioId: 0,
    avalId: undefined,
    fechaInicio: new Date().toISOString().split('T')[0],
    fechaFin: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    diaPago: 1,
    montoRenta: 0,
    montoDeposito: 0,
    montoFianza: 0,
    montoPenalidadDiaria: 0,
    diasGracia: 5,
    porcentajeIncrementoAnual: 5,
    condiciones: '',
    notas: ''
  });

  useEffect(() => {
    if (empresaActual) {
      loadInitialData();
    }
  }, [empresaActual, id]);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [propiedadesData, personasData] = await Promise.all([
        propiedadService.getAll(true, true), // Only available
        personaService.getAll(true)
      ]);
      setPropiedades(propiedadesData);
      setPersonas(personasData);

      if (isEditing && id) {
        const contrato = await contratoService.getById(parseInt(id));
        setFormData({
          numeroContrato: contrato.numeroContrato,
          propiedadId: contrato.propiedadId,
          arrendatarioId: contrato.arrendatarioId,
          avalId: contrato.avalId,
          fechaInicio: contrato.fechaInicio,
          fechaFin: contrato.fechaFin,
          diaPago: contrato.diaPago,
          montoRenta: contrato.montoRenta,
          montoDeposito: contrato.montoDeposito || 0,
          montoFianza: contrato.montoFianza || 0,
          montoPenalidadDiaria: contrato.montoPenalidadDiaria || 0,
          diasGracia: contrato.diasGracia || 0,
          porcentajeIncrementoAnual: contrato.porcentajeIncrementoAnual || 0,
          condiciones: contrato.condiciones || '',
          notas: contrato.notas || ''
        });
        // Add current property to list if editing
        if (!propiedadesData.find(p => p.id === contrato.propiedadId)) {
          const prop = await propiedadService.getById(contrato.propiedadId);
          setPropiedades([...propiedadesData, prop]);
        }
      }
      setError(null);
    } catch (err) {
      setError('Error al cargar datos');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (field: keyof CreateContratoRequest, value: unknown) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.propiedadId || !formData.arrendatarioId) {
      setError('Debe seleccionar propiedad y arrendatario');
      return;
    }

    try {
      setSaving(true);
      if (isEditing && id) {
        await contratoService.update(parseInt(id), formData as UpdateContratoRequest);
      } else {
        await contratoService.create(formData);
      }
      navigate('/contratos');
    } catch (err: unknown) {
      const errorMessage = err instanceof Error ? err.message : 'Error al guardar contrato';
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
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/contratos')}>
          Volver
        </Button>
        <Typography variant="h4">
          {isEditing ? 'Editar Contrato' : 'Nuevo Contrato'}
        </Typography>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid container spacing={3}>
            {/* Número de Contrato */}
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Número de Contrato"
                value={formData.numeroContrato}
                onChange={(e) => handleChange('numeroContrato', e.target.value)}
                helperText="Dejar vacío para generar automáticamente"
              />
            </Grid>

            {/* Propiedad */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel>Propiedad</InputLabel>
                <Select
                  value={formData.propiedadId || ''}
                  label="Propiedad"
                  onChange={(e) => handleChange('propiedadId', e.target.value)}
                >
                  {propiedades.map(prop => (
                    <MenuItem key={prop.id} value={prop.id}>
                      {prop.nombre} - {prop.direccionCompleta}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {/* Arrendatario */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel>Arrendatario</InputLabel>
                <Select
                  value={formData.arrendatarioId || ''}
                  label="Arrendatario"
                  onChange={(e) => handleChange('arrendatarioId', e.target.value)}
                >
                  {personas.map(persona => (
                    <MenuItem key={persona.id} value={persona.id}>
                      {persona.nombreCompleto}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {/* Aval */}
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth>
                <InputLabel>Aval (opcional)</InputLabel>
                <Select
                  value={formData.avalId || ''}
                  label="Aval (opcional)"
                  onChange={(e) => handleChange('avalId', e.target.value || undefined)}
                >
                  <MenuItem value="">Sin aval</MenuItem>
                  {personas.map(persona => (
                    <MenuItem key={persona.id} value={persona.id}>
                      {persona.nombreCompleto}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            {/* Fechas */}
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                required
                type="date"
                label="Fecha de Inicio"
                value={formData.fechaInicio}
                onChange={(e) => handleChange('fechaInicio', e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                required
                type="date"
                label="Fecha de Fin"
                value={formData.fechaFin}
                onChange={(e) => handleChange('fechaFin', e.target.value)}
                InputLabelProps={{ shrink: true }}
              />
            </Grid>

            {/* Día de Pago */}
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                required
                type="number"
                label="Día de Pago"
                value={formData.diaPago}
                onChange={(e) => handleChange('diaPago', parseInt(e.target.value))}
                inputProps={{ min: 1, max: 31 }}
              />
            </Grid>

            {/* Montos */}
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                required
                type="number"
                label="Monto de Renta"
                value={formData.montoRenta}
                onChange={(e) => handleChange('montoRenta', parseFloat(e.target.value))}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Depósito"
                value={formData.montoDeposito}
                onChange={(e) => handleChange('montoDeposito', parseFloat(e.target.value))}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Fianza"
                value={formData.montoFianza}
                onChange={(e) => handleChange('montoFianza', parseFloat(e.target.value))}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Penalidad Diaria"
                value={formData.montoPenalidadDiaria}
                onChange={(e) => handleChange('montoPenalidadDiaria', parseFloat(e.target.value))}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Días de Gracia"
                value={formData.diasGracia}
                onChange={(e) => handleChange('diasGracia', parseInt(e.target.value))}
              />
            </Grid>

            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Incremento Anual"
                value={formData.porcentajeIncrementoAnual}
                onChange={(e) => handleChange('porcentajeIncrementoAnual', parseFloat(e.target.value))}
                InputProps={{
                  endAdornment: <InputAdornment position="end">%</InputAdornment>
                }}
              />
            </Grid>

            {/* Condiciones */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={4}
                label="Condiciones del Contrato"
                value={formData.condiciones}
                onChange={(e) => handleChange('condiciones', e.target.value)}
              />
            </Grid>

            {/* Notas */}
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={2}
                label="Notas"
                value={formData.notas}
                onChange={(e) => handleChange('notas', e.target.value)}
              />
            </Grid>

            <Grid item xs={12}>
              <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
                <Button onClick={() => navigate('/contratos')}>
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

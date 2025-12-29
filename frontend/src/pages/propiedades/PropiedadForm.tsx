import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Box,
  Button,
  Paper,
  TextField,
  Typography,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  Divider,
  InputAdornment
} from '@mui/material';
import { Save as SaveIcon, ArrowBack as BackIcon } from '@mui/icons-material';
import { propiedadService } from '../../services/propiedadService';
import { TipoPropiedad, CreatePropiedadRequest, UpdatePropiedadRequest } from '../../types/propiedad';
import { useEmpresa } from '../../context/EmpresaContext';

export default function PropiedadForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { empresaActual } = useEmpresa();
  const isEdit = Boolean(id);

  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [tipos, setTipos] = useState<TipoPropiedad[]>([]);

  const [formData, setFormData] = useState<CreatePropiedadRequest>({
    tipoPropiedadId: 0,
    nombre: '',
    claveCatastral: '',
    calle: '',
    numeroExterior: '',
    numeroInterior: '',
    codigoPostal: '',
    referencias: '',
    superficieTerreno: undefined,
    superficieConstruccion: undefined,
    numRecamaras: undefined,
    numBanos: undefined,
    numEstacionamientos: undefined,
    numPisos: undefined,
    anioConstruccion: undefined,
    valorComercial: undefined,
    valorCatastral: undefined,
    rentaMensual: undefined,
    notas: ''
  });

  useEffect(() => {
    loadTipos();
    if (isEdit && id) {
      loadPropiedad(parseInt(id));
    }
  }, [id]);

  const loadTipos = async () => {
    try {
      const data = await propiedadService.getTipos();
      setTipos(data);
    } catch (err) {
      console.error('Error loading tipos:', err);
    }
  };

  const loadPropiedad = async (propiedadId: number) => {
    try {
      setLoading(true);
      const propiedad = await propiedadService.getById(propiedadId);
      setFormData({
        tipoPropiedadId: propiedad.tipoPropiedadId,
        nombre: propiedad.nombre,
        claveCatastral: propiedad.claveCatastral || '',
        calle: propiedad.calle || '',
        numeroExterior: propiedad.numeroExterior || '',
        numeroInterior: propiedad.numeroInterior || '',
        estadoId: propiedad.estadoId,
        municipioId: propiedad.municipioId,
        coloniaId: propiedad.coloniaId,
        codigoPostal: propiedad.codigoPostal || '',
        referencias: propiedad.referencias || '',
        superficieTerreno: propiedad.superficieTerreno,
        superficieConstruccion: propiedad.superficieConstruccion,
        numRecamaras: propiedad.numRecamaras,
        numBanos: propiedad.numBanos,
        numEstacionamientos: propiedad.numEstacionamientos,
        numPisos: propiedad.numPisos,
        anioConstruccion: propiedad.anioConstruccion,
        valorComercial: propiedad.valorComercial,
        valorCatastral: propiedad.valorCatastral,
        rentaMensual: propiedad.rentaMensual,
        notas: propiedad.notas || ''
      });
    } catch (err) {
      setError('Error al cargar la propiedad');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value ? parseFloat(value) : undefined
    }));
  };

  const handleSelectChange = (name: string, value: number | string) => {
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.tipoPropiedadId) {
      setError('Seleccione un tipo de propiedad');
      return;
    }

    if (!formData.nombre || !formData.calle) {
      setError('Nombre y calle son requeridos');
      return;
    }

    try {
      setSaving(true);
      setError(null);

      if (isEdit && id) {
        const updateData: UpdatePropiedadRequest = { ...formData };
        await propiedadService.update(parseInt(id), updateData);
      } else {
        await propiedadService.create(formData);
      }

      navigate('/propiedades');
    } catch (err) {
      setError(isEdit ? 'Error al actualizar la propiedad' : 'Error al crear la propiedad');
      console.error(err);
    } finally {
      setSaving(false);
    }
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

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Button startIcon={<BackIcon />} onClick={() => navigate('/propiedades')}>
          Volver
        </Button>
        <Typography variant="h4">
          {isEdit ? 'Editar Propiedad' : 'Nueva Propiedad'}
        </Typography>
      </Box>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Typography variant="h6" gutterBottom>Información General</Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={6}>
              <FormControl fullWidth required>
                <InputLabel>Tipo de Propiedad</InputLabel>
                <Select
                  value={formData.tipoPropiedadId || ''}
                  label="Tipo de Propiedad"
                  onChange={(e) => handleSelectChange('tipoPropiedadId', e.target.value as number)}
                >
                  {tipos.map(tipo => (
                    <MenuItem key={tipo.id} value={tipo.id}>{tipo.nombre}</MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                required
                label="Nombre"
                name="nombre"
                value={formData.nombre}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Clave Catastral"
                name="claveCatastral"
                value={formData.claveCatastral}
                onChange={handleChange}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />
          <Typography variant="h6" gutterBottom>Dirección</Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                required
                label="Calle"
                name="calle"
                value={formData.calle}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                label="Número Exterior"
                name="numeroExterior"
                value={formData.numeroExterior}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                label="Número Interior"
                name="numeroInterior"
                value={formData.numeroInterior}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                label="Código Postal"
                name="codigoPostal"
                value={formData.codigoPostal}
                onChange={handleChange}
              />
            </Grid>
            <Grid item xs={12} sm={9}>
              <TextField
                fullWidth
                label="Referencias"
                name="referencias"
                value={formData.referencias}
                onChange={handleChange}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />
          <Typography variant="h6" gutterBottom>Características</Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Superficie Terreno"
                name="superficieTerreno"
                value={formData.superficieTerreno || ''}
                onChange={handleNumberChange}
                InputProps={{
                  endAdornment: <InputAdornment position="end">m²</InputAdornment>
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Superficie Construcción"
                name="superficieConstruccion"
                value={formData.superficieConstruccion || ''}
                onChange={handleNumberChange}
                InputProps={{
                  endAdornment: <InputAdornment position="end">m²</InputAdornment>
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Año Construcción"
                name="anioConstruccion"
                value={formData.anioConstruccion || ''}
                onChange={handleNumberChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                type="number"
                label="Recámaras"
                name="numRecamaras"
                value={formData.numRecamaras || ''}
                onChange={handleNumberChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                type="number"
                label="Baños"
                name="numBanos"
                value={formData.numBanos || ''}
                onChange={handleNumberChange}
                inputProps={{ step: 0.5 }}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                type="number"
                label="Estacionamientos"
                name="numEstacionamientos"
                value={formData.numEstacionamientos || ''}
                onChange={handleNumberChange}
              />
            </Grid>
            <Grid item xs={12} sm={3}>
              <TextField
                fullWidth
                type="number"
                label="Pisos"
                name="numPisos"
                value={formData.numPisos || ''}
                onChange={handleNumberChange}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />
          <Typography variant="h6" gutterBottom>Valores</Typography>
          <Grid container spacing={2} sx={{ mb: 3 }}>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Valor Comercial"
                name="valorComercial"
                value={formData.valorComercial || ''}
                onChange={handleNumberChange}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Valor Catastral"
                name="valorCatastral"
                value={formData.valorCatastral || ''}
                onChange={handleNumberChange}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>
            <Grid item xs={12} sm={4}>
              <TextField
                fullWidth
                type="number"
                label="Renta Mensual"
                name="rentaMensual"
                value={formData.rentaMensual || ''}
                onChange={handleNumberChange}
                InputProps={{
                  startAdornment: <InputAdornment position="start">$</InputAdornment>
                }}
              />
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <TextField
                fullWidth
                multiline
                rows={3}
                label="Notas"
                name="notas"
                value={formData.notas}
                onChange={handleChange}
              />
            </Grid>
          </Grid>

          <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button onClick={() => navigate('/propiedades')}>
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
        </form>
      </Paper>
    </Box>
  );
}

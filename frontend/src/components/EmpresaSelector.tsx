import { FormControl, InputLabel, Select, MenuItem, SelectChangeEvent, Box, CircularProgress } from '@mui/material';
import { useEmpresa } from '../context/EmpresaContext';

export default function EmpresaSelector() {
  const { empresas, empresaActual, loading, setEmpresaActual } = useEmpresa();

  const handleChange = (event: SelectChangeEvent<number>) => {
    const empresaId = event.target.value as number;
    const selected = empresas.find(e => e.id === empresaId);
    if (selected) {
      setEmpresaActual(selected);
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', alignItems: 'center', minWidth: 200 }}>
        <CircularProgress size={20} color="inherit" />
      </Box>
    );
  }

  if (empresas.length === 0) {
    return null;
  }

  return (
    <FormControl size="small" sx={{ minWidth: 200 }}>
      <InputLabel id="empresa-selector-label" sx={{ color: 'inherit' }}>
        Empresa
      </InputLabel>
      <Select
        labelId="empresa-selector-label"
        value={empresaActual?.id || ''}
        label="Empresa"
        onChange={handleChange}
        sx={{
          color: 'inherit',
          '.MuiOutlinedInput-notchedOutline': {
            borderColor: 'rgba(255, 255, 255, 0.5)',
          },
          '&:hover .MuiOutlinedInput-notchedOutline': {
            borderColor: 'rgba(255, 255, 255, 0.8)',
          },
          '.MuiSvgIcon-root': {
            color: 'inherit',
          },
        }}
      >
        {empresas.map((empresa) => (
          <MenuItem key={empresa.id} value={empresa.id}>
            {empresa.nombre}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}

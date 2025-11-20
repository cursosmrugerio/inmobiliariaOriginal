import { Box, Typography, Paper, Grid, Card, CardContent, Divider } from '@mui/material';
import { Business, Email, Phone, LocationOn } from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { useEmpresa } from '../context/EmpresaContext';

export default function Dashboard() {
  const { user } = useAuth();
  const { empresaActual } = useEmpresa();

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        Dashboard
      </Typography>
      <Typography variant="body1" color="textSecondary" gutterBottom>
        Bienvenido, {user?.nombre} {user?.apellido}
      </Typography>

      {empresaActual && (
        <Card sx={{ mb: 3, mt: 2 }}>
          <CardContent>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Business sx={{ mr: 1 }} color="primary" />
              <Typography variant="h6">
                {empresaActual.nombre}
              </Typography>
            </Box>
            <Divider sx={{ mb: 2 }} />
            <Grid container spacing={2}>
              {empresaActual.rfc && (
                <Grid item xs={12} sm={6} md={3}>
                  <Typography variant="body2" color="textSecondary">RFC</Typography>
                  <Typography variant="body1">{empresaActual.rfc}</Typography>
                </Grid>
              )}
              {empresaActual.email && (
                <Grid item xs={12} sm={6} md={3}>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Email fontSize="small" sx={{ mr: 0.5 }} color="action" />
                    <Typography variant="body2" color="textSecondary">Email</Typography>
                  </Box>
                  <Typography variant="body1">{empresaActual.email}</Typography>
                </Grid>
              )}
              {empresaActual.telefono && (
                <Grid item xs={12} sm={6} md={3}>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Phone fontSize="small" sx={{ mr: 0.5 }} color="action" />
                    <Typography variant="body2" color="textSecondary">Telefono</Typography>
                  </Box>
                  <Typography variant="body1">{empresaActual.telefono}</Typography>
                </Grid>
              )}
              {empresaActual.direccion && (
                <Grid item xs={12} sm={6} md={3}>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <LocationOn fontSize="small" sx={{ mr: 0.5 }} color="action" />
                    <Typography variant="body2" color="textSecondary">Direccion</Typography>
                  </Box>
                  <Typography variant="body1">{empresaActual.direccion}</Typography>
                </Grid>
              )}
            </Grid>
          </CardContent>
        </Card>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6">Propiedades</Typography>
            <Typography variant="h3">0</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6">Contratos Activos</Typography>
            <Typography variant="h3">0</Typography>
          </Paper>
        </Grid>
        <Grid item xs={12} md={4}>
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6">Pagos Pendientes</Typography>
            <Typography variant="h3">0</Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Layout from './components/Layout';
import PersonasList from './pages/personas/PersonasList';
import PersonaForm from './pages/personas/PersonaForm';
import PersonaDetail from './pages/personas/PersonaDetail';
import PropiedadesList from './pages/propiedades/PropiedadesList';
import PropiedadForm from './pages/propiedades/PropiedadForm';
import PropiedadDetail from './pages/propiedades/PropiedadDetail';
import ContratosList from './pages/contratos/ContratosList';
import ContratoForm from './pages/contratos/ContratoForm';
import ContratoDetail from './pages/contratos/ContratoDetail';
import { PagosList, PagoForm, PagoDetail, CargosList, CargoForm } from './pages/pagos';
import ReportesPage from './pages/reportes/ReportesPage';
import { NotificacionesDashboard } from './pages/notificaciones';
import MantenimientoPage from './pages/mantenimiento/MantenimientoPage';

function App() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Cargando...</div>;
  }

  return (
    <Routes>
      <Route path="/login" element={
        isAuthenticated ? <Navigate to="/dashboard" /> : <Login />
      } />
      <Route path="/" element={
        isAuthenticated ? <Layout /> : <Navigate to="/login" />
      }>
        <Route index element={<Navigate to="/dashboard" />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="personas" element={<PersonasList />} />
        <Route path="personas/new" element={<PersonaForm />} />
        <Route path="personas/:id" element={<PersonaDetail />} />
        <Route path="personas/:id/edit" element={<PersonaForm />} />
        <Route path="propiedades" element={<PropiedadesList />} />
        <Route path="propiedades/new" element={<PropiedadForm />} />
        <Route path="propiedades/:id" element={<PropiedadDetail />} />
        <Route path="propiedades/:id/edit" element={<PropiedadForm />} />
        <Route path="contratos" element={<ContratosList />} />
        <Route path="contratos/new" element={<ContratoForm />} />
        <Route path="contratos/:id" element={<ContratoDetail />} />
        <Route path="contratos/:id/edit" element={<ContratoForm />} />
        <Route path="pagos" element={<PagosList />} />
        <Route path="pagos/nuevo" element={<PagoForm />} />
        <Route path="pagos/:id" element={<PagoDetail />} />
        <Route path="pagos/cargos" element={<CargosList />} />
        <Route path="pagos/cargos/nuevo" element={<CargoForm />} />
        <Route path="reportes" element={<ReportesPage />} />
        <Route path="notificaciones" element={<NotificacionesDashboard />} />
        <Route path="mantenimiento" element={<MantenimientoPage />} />
      </Route>
    </Routes>
  );
}

export default App;

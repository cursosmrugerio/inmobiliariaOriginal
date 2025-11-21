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
import ReportesPage from './pages/reportes/ReportesPage';
import { NotificacionesDashboard } from './pages/notificaciones';

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
        <Route path="reportes" element={<ReportesPage />} />
        <Route path="notificaciones" element={<NotificacionesDashboard />} />
      </Route>
    </Routes>
  );
}

export default App;

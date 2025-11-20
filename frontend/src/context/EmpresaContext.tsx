import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { empresaService, Empresa } from '../services/empresaService';
import { useAuth } from './AuthContext';

interface EmpresaContextType {
  empresas: Empresa[];
  empresaActual: Empresa | null;
  loading: boolean;
  error: string | null;
  setEmpresaActual: (empresa: Empresa | null) => void;
  refreshEmpresas: () => Promise<void>;
}

const EmpresaContext = createContext<EmpresaContextType | undefined>(undefined);

export function EmpresaProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated, user } = useAuth();
  const [empresas, setEmpresas] = useState<Empresa[]>([]);
  const [empresaActual, setEmpresaActual] = useState<Empresa | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refreshEmpresas = async () => {
    if (!isAuthenticated) return;

    setLoading(true);
    setError(null);
    try {
      const data = await empresaService.getAll();
      setEmpresas(data);

      // Auto-select user's empresa or first available
      if (!empresaActual && data.length > 0) {
        const userEmpresa = user?.empresaId
          ? data.find(e => e.id === user.empresaId)
          : null;
        setEmpresaActual(userEmpresa || data[0]);
      }
    } catch (err) {
      setError('Error al cargar empresas');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshEmpresas();
  }, [isAuthenticated]);

  return (
    <EmpresaContext.Provider value={{
      empresas,
      empresaActual,
      loading,
      error,
      setEmpresaActual,
      refreshEmpresas
    }}>
      {children}
    </EmpresaContext.Provider>
  );
}

export function useEmpresa() {
  const context = useContext(EmpresaContext);
  if (context === undefined) {
    throw new Error('useEmpresa must be used within an EmpresaProvider');
  }
  return context;
}

import api from './api';

export interface Empresa {
  id: number;
  nombre: string;
  rfc: string | null;
  direccion: string | null;
  telefono: string | null;
  email: string | null;
  activo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateEmpresaRequest {
  nombre: string;
  rfc?: string;
  direccion?: string;
  telefono?: string;
  email?: string;
}

export interface UpdateEmpresaRequest {
  nombre?: string;
  rfc?: string;
  direccion?: string;
  telefono?: string;
  email?: string;
  activo?: boolean;
}

export const empresaService = {
  getAll: async (activeOnly = true): Promise<Empresa[]> => {
    const response = await api.get<Empresa[]>('/api/empresas', {
      params: { activeOnly }
    });
    return response.data;
  },

  getById: async (id: number): Promise<Empresa> => {
    const response = await api.get<Empresa>(`/api/empresas/${id}`);
    return response.data;
  },

  create: async (data: CreateEmpresaRequest): Promise<Empresa> => {
    const response = await api.post<Empresa>('/api/empresas', data);
    return response.data;
  },

  update: async (id: number, data: UpdateEmpresaRequest): Promise<Empresa> => {
    const response = await api.put<Empresa>(`/api/empresas/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/empresas/${id}`);
  }
};

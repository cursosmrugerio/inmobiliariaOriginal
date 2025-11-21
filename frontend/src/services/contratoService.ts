import api from './api';
import {
  Contrato,
  CreateContratoRequest,
  UpdateContratoRequest,
  RenovarContratoRequest,
  ContratoStats,
  EstadoContrato
} from '../types/contrato';

export const contratoService = {
  // CRUD
  getAll: async (activeOnly = true, estado?: EstadoContrato): Promise<Contrato[]> => {
    const params: Record<string, unknown> = { activeOnly };
    if (estado) params.estado = estado;
    const response = await api.get<Contrato[]>('/api/contratos', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Contrato> => {
    const response = await api.get<Contrato>(`/api/contratos/${id}`);
    return response.data;
  },

  getByPropiedad: async (propiedadId: number): Promise<Contrato[]> => {
    const response = await api.get<Contrato[]>('/api/contratos', { params: { propiedadId } });
    return response.data;
  },

  getByArrendatario: async (arrendatarioId: number): Promise<Contrato[]> => {
    const response = await api.get<Contrato[]>('/api/contratos', { params: { arrendatarioId } });
    return response.data;
  },

  create: async (data: CreateContratoRequest): Promise<Contrato> => {
    const response = await api.post<Contrato>('/api/contratos', data);
    return response.data;
  },

  update: async (id: number, data: UpdateContratoRequest): Promise<Contrato> => {
    const response = await api.put<Contrato>(`/api/contratos/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/contratos/${id}`);
  },

  // Lifecycle
  activar: async (id: number): Promise<Contrato> => {
    const response = await api.post<Contrato>(`/api/contratos/${id}/activar`);
    return response.data;
  },

  terminar: async (id: number, motivo?: string): Promise<Contrato> => {
    const response = await api.post<Contrato>(`/api/contratos/${id}/terminar`, { motivo });
    return response.data;
  },

  cancelar: async (id: number, motivo?: string): Promise<Contrato> => {
    const response = await api.post<Contrato>(`/api/contratos/${id}/cancelar`, { motivo });
    return response.data;
  },

  renovar: async (id: number, data: RenovarContratoRequest): Promise<Contrato> => {
    const response = await api.post<Contrato>(`/api/contratos/${id}/renovar`, data);
    return response.data;
  },

  // Vencimientos
  getPorVencer: async (dias = 30): Promise<Contrato[]> => {
    const response = await api.get<Contrato[]>('/api/contratos/por-vencer', { params: { dias } });
    return response.data;
  },

  getVencidos: async (): Promise<Contrato[]> => {
    const response = await api.get<Contrato[]>('/api/contratos/vencidos');
    return response.data;
  },

  // Stats
  getEstadisticas: async (): Promise<ContratoStats> => {
    const response = await api.get<ContratoStats>('/api/contratos/estadisticas');
    return response.data;
  }
};

import api from './api';
import {
  Persona,
  PersonaRol,
  Direccion,
  CuentaBancaria,
  CreatePersonaRequest,
  UpdatePersonaRequest,
  CreateDireccionRequest,
  CreateCuentaBancariaRequest
} from '../types/persona';

export const personaService = {
  // Personas
  getAll: async (activeOnly = true, rolId?: number): Promise<Persona[]> => {
    const params: Record<string, unknown> = { activeOnly };
    if (rolId) params.rolId = rolId;
    const response = await api.get<Persona[]>('/api/personas', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Persona> => {
    const response = await api.get<Persona>(`/api/personas/${id}`);
    return response.data;
  },

  create: async (data: CreatePersonaRequest): Promise<Persona> => {
    const response = await api.post<Persona>('/api/personas', data);
    return response.data;
  },

  update: async (id: number, data: UpdatePersonaRequest): Promise<Persona> => {
    const response = await api.put<Persona>(`/api/personas/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/personas/${id}`);
  },

  // Roles
  getRoles: async (personaId: number): Promise<PersonaRol[]> => {
    const response = await api.get<PersonaRol[]>(`/api/personas/${personaId}/roles`);
    return response.data;
  },

  addRol: async (personaId: number, rolId: number): Promise<PersonaRol> => {
    const response = await api.post<PersonaRol>(`/api/personas/${personaId}/roles/${rolId}`);
    return response.data;
  },

  removeRol: async (personaId: number, rolId: number): Promise<void> => {
    await api.delete(`/api/personas/${personaId}/roles/${rolId}`);
  },

  // Direcciones
  getDirecciones: async (personaId: number): Promise<Direccion[]> => {
    const response = await api.get<Direccion[]>(`/api/personas/${personaId}/direcciones`);
    return response.data;
  },

  addDireccion: async (personaId: number, data: CreateDireccionRequest): Promise<Direccion> => {
    const response = await api.post<Direccion>(`/api/personas/${personaId}/direcciones`, data);
    return response.data;
  },

  updateDireccion: async (personaId: number, direccionId: number, data: Partial<CreateDireccionRequest>): Promise<Direccion> => {
    const response = await api.put<Direccion>(`/api/personas/${personaId}/direcciones/${direccionId}`, data);
    return response.data;
  },

  deleteDireccion: async (personaId: number, direccionId: number): Promise<void> => {
    await api.delete(`/api/personas/${personaId}/direcciones/${direccionId}`);
  },

  // Cuentas Bancarias
  getCuentasBancarias: async (personaId: number): Promise<CuentaBancaria[]> => {
    const response = await api.get<CuentaBancaria[]>(`/api/personas/${personaId}/cuentas-bancarias`);
    return response.data;
  },

  addCuentaBancaria: async (personaId: number, data: CreateCuentaBancariaRequest): Promise<CuentaBancaria> => {
    const response = await api.post<CuentaBancaria>(`/api/personas/${personaId}/cuentas-bancarias`, data);
    return response.data;
  },

  updateCuentaBancaria: async (personaId: number, cuentaId: number, data: Partial<CreateCuentaBancariaRequest>): Promise<CuentaBancaria> => {
    const response = await api.put<CuentaBancaria>(`/api/personas/${personaId}/cuentas-bancarias/${cuentaId}`, data);
    return response.data;
  },

  deleteCuentaBancaria: async (personaId: number, cuentaId: number): Promise<void> => {
    await api.delete(`/api/personas/${personaId}/cuentas-bancarias/${cuentaId}`);
  }
};

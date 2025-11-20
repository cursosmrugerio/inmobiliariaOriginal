import api from './api';
import {
  Propiedad,
  CreatePropiedadRequest,
  UpdatePropiedadRequest,
  TipoPropiedad,
  PropiedadPropietario,
  AddPropietarioRequest
} from '../types/propiedad';

export const propiedadService = {
  // Propiedades CRUD
  getAll: async (activeOnly = true, disponible?: boolean): Promise<Propiedad[]> => {
    const params: Record<string, unknown> = { activeOnly };
    if (disponible !== undefined) params.disponible = disponible;
    const response = await api.get<Propiedad[]>('/api/propiedades', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Propiedad> => {
    const response = await api.get<Propiedad>(`/api/propiedades/${id}`);
    return response.data;
  },

  getByTipo: async (tipoId: number): Promise<Propiedad[]> => {
    const response = await api.get<Propiedad[]>('/api/propiedades', { params: { tipoId } });
    return response.data;
  },

  getByPropietario: async (propietarioId: number): Promise<Propiedad[]> => {
    const response = await api.get<Propiedad[]>('/api/propiedades', { params: { propietarioId } });
    return response.data;
  },

  create: async (data: CreatePropiedadRequest): Promise<Propiedad> => {
    const response = await api.post<Propiedad>('/api/propiedades', data);
    return response.data;
  },

  update: async (id: number, data: UpdatePropiedadRequest): Promise<Propiedad> => {
    const response = await api.put<Propiedad>(`/api/propiedades/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/propiedades/${id}`);
  },

  // Propietarios
  getPropietarios: async (propiedadId: number): Promise<PropiedadPropietario[]> => {
    const response = await api.get<PropiedadPropietario[]>(`/api/propiedades/${propiedadId}/propietarios`);
    return response.data;
  },

  addPropietario: async (propiedadId: number, data: AddPropietarioRequest): Promise<PropiedadPropietario> => {
    const response = await api.post<PropiedadPropietario>(`/api/propiedades/${propiedadId}/propietarios`, data);
    return response.data;
  },

  removePropietario: async (propiedadId: number, propietarioId: number): Promise<void> => {
    await api.delete(`/api/propiedades/${propiedadId}/propietarios/${propietarioId}`);
  },

  // Catálogos
  getTipos: async (): Promise<TipoPropiedad[]> => {
    const response = await api.get<TipoPropiedad[]>('/api/propiedades/tipos');
    return response.data;
  }
};

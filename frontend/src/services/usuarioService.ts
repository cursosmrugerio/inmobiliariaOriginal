import api from './api';
import type { Usuario, CreateUsuarioRequest, UpdateUsuarioRequest, ChangeRolRequest } from '../types/usuario';

export const usuarioService = {
  getAll: async (activo?: boolean): Promise<Usuario[]> => {
    const params = activo !== undefined ? { activo } : {};
    const response = await api.get<Usuario[]>('/api/usuarios', { params });
    return response.data;
  },

  getById: async (id: number): Promise<Usuario> => {
    const response = await api.get<Usuario>(`/api/usuarios/${id}`);
    return response.data;
  },

  create: async (data: CreateUsuarioRequest): Promise<Usuario> => {
    const response = await api.post<Usuario>('/api/usuarios', data);
    return response.data;
  },

  update: async (id: number, data: UpdateUsuarioRequest): Promise<Usuario> => {
    const response = await api.put<Usuario>(`/api/usuarios/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/api/usuarios/${id}`);
  },

  cambiarRol: async (id: number, rol: ChangeRolRequest): Promise<Usuario> => {
    const response = await api.patch<Usuario>(`/api/usuarios/${id}/rol`, rol);
    return response.data;
  },

  toggleActivo: async (id: number): Promise<Usuario> => {
    const response = await api.patch<Usuario>(`/api/usuarios/${id}/toggle-activo`);
    return response.data;
  }
};

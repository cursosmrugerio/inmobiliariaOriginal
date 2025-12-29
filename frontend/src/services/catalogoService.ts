import api from './api';

export interface Estado {
  id: number;
  nombre: string;
  abreviatura: string;
}

export interface Municipio {
  id: number;
  nombre: string;
  estadoId: number;
}

export interface Colonia {
  id: number;
  nombre: string;
  codigoPostal: string;
  municipioId: number;
}

export const catalogoService = {
  // Estados
  getEstados: async (): Promise<Estado[]> => {
    const response = await api.get<Estado[]>('/api/catalogos/estados');
    return response.data;
  },

  getEstadoById: async (id: number): Promise<Estado> => {
    const response = await api.get<Estado>(`/api/catalogos/estados/${id}`);
    return response.data;
  },

  // Municipios
  getMunicipiosByEstado: async (estadoId: number): Promise<Municipio[]> => {
    const response = await api.get<Municipio[]>(`/api/catalogos/estados/${estadoId}/municipios`);
    return response.data;
  },

  // Colonias
  getColoniasByMunicipio: async (municipioId: number): Promise<Colonia[]> => {
    const response = await api.get<Colonia[]>(`/api/catalogos/municipios/${municipioId}/colonias`);
    return response.data;
  }
};

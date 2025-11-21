import api from './api';

export interface Estado {
  id: number;
  clave: string;
  nombre: string;
  activo: boolean;
}

export interface Municipio {
  id: number;
  estadoId: number;
  clave: string;
  nombre: string;
  activo: boolean;
}

export interface Colonia {
  id: number;
  municipioId: number;
  tipoAsentamientoId: number;
  nombre: string;
  codigoPostal: string;
  activo: boolean;
}

export interface CodigoPostal {
  id: number;
  codigo: string;
  municipioId: number;
  activo: boolean;
}

export interface TipoAsentamiento {
  id: number;
  nombre: string;
  activo: boolean;
}

export interface Rol {
  id: number;
  clave: string;
  nombre: string;
  descripcion: string | null;
  activo: boolean;
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
  getMunicipios: async (estadoId?: number): Promise<Municipio[]> => {
    const response = await api.get<Municipio[]>('/api/catalogos/municipios', {
      params: estadoId ? { estadoId } : undefined
    });
    return response.data;
  },

  getMunicipioById: async (id: number): Promise<Municipio> => {
    const response = await api.get<Municipio>(`/api/catalogos/municipios/${id}`);
    return response.data;
  },

  // Colonias
  getColonias: async (municipioId?: number, codigoPostal?: string): Promise<Colonia[]> => {
    const params: { municipioId?: number; codigoPostal?: string } = {};
    if (municipioId) params.municipioId = municipioId;
    if (codigoPostal) params.codigoPostal = codigoPostal;

    const response = await api.get<Colonia[]>('/api/catalogos/colonias', {
      params: Object.keys(params).length > 0 ? params : undefined
    });
    return response.data;
  },

  getColoniaById: async (id: number): Promise<Colonia> => {
    const response = await api.get<Colonia>(`/api/catalogos/colonias/${id}`);
    return response.data;
  },

  // Códigos Postales
  getCodigosPostales: async (codigo?: string, municipioId?: number): Promise<CodigoPostal[]> => {
    const params: { codigo?: string; municipioId?: number } = {};
    if (codigo) params.codigo = codigo;
    if (municipioId) params.municipioId = municipioId;

    const response = await api.get<CodigoPostal[]>('/api/catalogos/codigos-postales', {
      params: Object.keys(params).length > 0 ? params : undefined
    });
    return response.data;
  },

  getCodigoPostalById: async (id: number): Promise<CodigoPostal> => {
    const response = await api.get<CodigoPostal>(`/api/catalogos/codigos-postales/${id}`);
    return response.data;
  },

  // Tipos de Asentamiento
  getTiposAsentamiento: async (): Promise<TipoAsentamiento[]> => {
    const response = await api.get<TipoAsentamiento[]>('/api/catalogos/tipos-asentamiento');
    return response.data;
  },

  getTipoAsentamientoById: async (id: number): Promise<TipoAsentamiento> => {
    const response = await api.get<TipoAsentamiento>(`/api/catalogos/tipos-asentamiento/${id}`);
    return response.data;
  },

  // Roles
  getRoles: async (): Promise<Rol[]> => {
    const response = await api.get<Rol[]>('/api/catalogos/roles');
    return response.data;
  },

  getRolById: async (id: number): Promise<Rol> => {
    const response = await api.get<Rol>(`/api/catalogos/roles/${id}`);
    return response.data;
  },

  getRolByClave: async (clave: string): Promise<Rol> => {
    const response = await api.get<Rol>(`/api/catalogos/roles/clave/${clave}`);
    return response.data;
  }
};

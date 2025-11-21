import api from './api';
import type {
  CarteraVencida,
  SeguimientoCobranza,
  ProyeccionCobranza,
  ResumenCobranza,
  CreateCarteraVencidaRequest,
  CreateSeguimientoRequest
} from '../types/cobranza';

export const cobranzaService = {
  // Cartera Vencida
  getAllCarteraVencida: async (activeOnly = true): Promise<CarteraVencida[]> => {
    const response = await api.get<CarteraVencida[]>('/api/cobranza/cartera', {
      params: { activeOnly }
    });
    return response.data;
  },

  getCarteraVencidaById: async (id: number): Promise<CarteraVencida> => {
    const response = await api.get<CarteraVencida>(`/api/cobranza/cartera/${id}`);
    return response.data;
  },

  createCarteraVencida: async (data: CreateCarteraVencidaRequest): Promise<CarteraVencida> => {
    const response = await api.post<CarteraVencida>('/api/cobranza/cartera', data);
    return response.data;
  },

  updateEstadoCobranza: async (id: number, estadoCobranza: string): Promise<CarteraVencida> => {
    const response = await api.patch<CarteraVencida>(`/api/cobranza/cartera/${id}/estado`, null, {
      params: { estadoCobranza }
    });
    return response.data;
  },

  registrarPago: async (id: number, monto: number): Promise<CarteraVencida> => {
    const response = await api.post<CarteraVencida>(`/api/cobranza/cartera/${id}/pago`, null, {
      params: { monto }
    });
    return response.data;
  },

  calcularPenalidad: async (id: number): Promise<CarteraVencida> => {
    const response = await api.post<CarteraVencida>(`/api/cobranza/cartera/${id}/penalidad`);
    return response.data;
  },

  deleteCarteraVencida: async (id: number): Promise<void> => {
    await api.delete(`/api/cobranza/cartera/${id}`);
  },

  getCarteraByPersona: async (personaId: number): Promise<CarteraVencida[]> => {
    const response = await api.get<CarteraVencida[]>(`/api/cobranza/cartera/persona/${personaId}`);
    return response.data;
  },

  getCarteraByPropiedad: async (propiedadId: number): Promise<CarteraVencida[]> => {
    const response = await api.get<CarteraVencida[]>(`/api/cobranza/cartera/propiedad/${propiedadId}`);
    return response.data;
  },

  getCarteraByEstado: async (estado: string): Promise<CarteraVencida[]> => {
    const response = await api.get<CarteraVencida[]>(`/api/cobranza/cartera/estado/${estado}`);
    return response.data;
  },

  getCarteraByClasificacion: async (clasificacion: string): Promise<CarteraVencida[]> => {
    const response = await api.get<CarteraVencida[]>(`/api/cobranza/cartera/clasificacion/${clasificacion}`);
    return response.data;
  },

  // Resumen
  getResumenCobranza: async (): Promise<ResumenCobranza> => {
    const response = await api.get<ResumenCobranza>('/api/cobranza/resumen');
    return response.data;
  },

  // Seguimiento
  getSeguimientoByCartera: async (carteraVencidaId: number): Promise<SeguimientoCobranza[]> => {
    const response = await api.get<SeguimientoCobranza[]>(`/api/cobranza/seguimiento/cartera/${carteraVencidaId}`);
    return response.data;
  },

  createSeguimiento: async (data: CreateSeguimientoRequest): Promise<SeguimientoCobranza> => {
    const response = await api.post<SeguimientoCobranza>('/api/cobranza/seguimiento', data);
    return response.data;
  },

  getAccionesPendientes: async (): Promise<SeguimientoCobranza[]> => {
    const response = await api.get<SeguimientoCobranza[]>('/api/cobranza/seguimiento/pendientes');
    return response.data;
  },

  // Proyección
  getProyecciones: async (periodoInicio: string, periodoFin: string): Promise<ProyeccionCobranza[]> => {
    const response = await api.get<ProyeccionCobranza[]>('/api/cobranza/proyeccion', {
      params: { periodoInicio, periodoFin }
    });
    return response.data;
  },

  createOrUpdateProyeccion: async (
    periodo: string,
    montoProyectado: number,
    cantidadContratos: number,
    cantidadPagosEsperados: number
  ): Promise<ProyeccionCobranza> => {
    const response = await api.post<ProyeccionCobranza>('/api/cobranza/proyeccion', null, {
      params: { periodo, montoProyectado, cantidadContratos, cantidadPagosEsperados }
    });
    return response.data;
  },

  actualizarProyeccionCobrado: async (
    periodo: string,
    montoCobrado: number,
    pagosRecibidos: number
  ): Promise<ProyeccionCobranza> => {
    const response = await api.patch<ProyeccionCobranza>('/api/cobranza/proyeccion/cobrado', null, {
      params: { periodo, montoCobrado, pagosRecibidos }
    });
    return response.data;
  }
};

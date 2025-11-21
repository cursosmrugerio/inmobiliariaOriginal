import api from './api';
import {
  Pago,
  Cargo,
  CreatePagoRequest,
  CreateCargoRequest,
  AplicarPagoRequest,
  GenerarCargosFijosRequest,
  PagoEstadisticas,
} from '../types/pago';

export const pagoService = {
  // Pagos
  getAll: async (): Promise<Pago[]> => {
    const response = await api.get<Pago[]>('/api/pagos');
    return response.data;
  },

  getById: async (id: number): Promise<Pago> => {
    const response = await api.get<Pago>(`/api/pagos/${id}`);
    return response.data;
  },

  getByContrato: async (contratoId: number): Promise<Pago[]> => {
    const response = await api.get<Pago[]>(`/api/pagos/contrato/${contratoId}`);
    return response.data;
  },

  getByPeriodo: async (fechaInicio: string, fechaFin: string): Promise<Pago[]> => {
    const response = await api.get<Pago[]>('/api/pagos/periodo', {
      params: { fechaInicio, fechaFin },
    });
    return response.data;
  },

  create: async (data: CreatePagoRequest): Promise<Pago> => {
    const response = await api.post<Pago>('/api/pagos', data);
    return response.data;
  },

  aplicar: async (id: number, data: AplicarPagoRequest): Promise<Pago> => {
    const response = await api.post<Pago>(`/api/pagos/${id}/aplicar`, data);
    return response.data;
  },

  cancelar: async (id: number): Promise<void> => {
    await api.post(`/api/pagos/${id}/cancelar`);
  },

  // Cargos
  getAllCargos: async (): Promise<Cargo[]> => {
    const response = await api.get<Cargo[]>('/api/pagos/cargos');
    return response.data;
  },

  getCargoById: async (id: number): Promise<Cargo> => {
    const response = await api.get<Cargo>(`/api/pagos/cargos/${id}`);
    return response.data;
  },

  getCargosByContrato: async (contratoId: number): Promise<Cargo[]> => {
    const response = await api.get<Cargo[]>(`/api/pagos/cargos/contrato/${contratoId}`);
    return response.data;
  },

  getCargosPendientes: async (): Promise<Cargo[]> => {
    const response = await api.get<Cargo[]>('/api/pagos/cargos/pendientes');
    return response.data;
  },

  getCargosVencidos: async (): Promise<Cargo[]> => {
    const response = await api.get<Cargo[]>('/api/pagos/cargos/vencidos');
    return response.data;
  },

  createCargo: async (data: CreateCargoRequest): Promise<Cargo> => {
    const response = await api.post<Cargo>('/api/pagos/cargos', data);
    return response.data;
  },

  cancelarCargo: async (id: number): Promise<void> => {
    await api.post(`/api/pagos/cargos/${id}/cancelar`);
  },

  generarCargosFijos: async (data: GenerarCargosFijosRequest): Promise<Cargo[]> => {
    const response = await api.post<Cargo[]>('/api/pagos/cargos/generar-fijos', data);
    return response.data;
  },

  actualizarCargosVencidos: async (): Promise<void> => {
    await api.post('/api/pagos/cargos/actualizar-vencidos');
  },

  // Estadísticas
  getEstadisticas: async (): Promise<PagoEstadisticas> => {
    const response = await api.get<PagoEstadisticas>('/api/pagos/estadisticas');
    return response.data;
  },

  getSaldoPendiente: async (contratoId: number): Promise<number> => {
    const response = await api.get<number>(`/api/pagos/saldo-pendiente/${contratoId}`);
    return response.data;
  },
};

export default pagoService;

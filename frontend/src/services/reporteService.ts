import api from './api';
import type {
  EstadoCuenta,
  AntiguedadSaldos,
  ReporteCarteraVencida,
  ProyeccionCobranzaReporte
} from '../types/reporte';

export const reporteService = {
  // Estado de Cuenta
  getEstadoCuenta: async (
    personaId: number,
    fechaInicio?: string,
    fechaFin?: string
  ): Promise<EstadoCuenta> => {
    const response = await api.get<EstadoCuenta>(`/api/reportes/estado-cuenta/${personaId}`, {
      params: { fechaInicio, fechaFin }
    });
    return response.data;
  },

  exportEstadoCuentaExcel: async (
    personaId: number,
    fechaInicio?: string,
    fechaFin?: string
  ): Promise<Blob> => {
    const response = await api.get(`/api/reportes/estado-cuenta/${personaId}/excel`, {
      params: { fechaInicio, fechaFin },
      responseType: 'blob'
    });
    return response.data;
  },

  exportEstadoCuentaCsv: async (
    personaId: number,
    fechaInicio?: string,
    fechaFin?: string
  ): Promise<Blob> => {
    const response = await api.get(`/api/reportes/estado-cuenta/${personaId}/csv`, {
      params: { fechaInicio, fechaFin },
      responseType: 'blob'
    });
    return response.data;
  },

  // Antiguedad de Saldos
  getAntiguedadSaldos: async (fechaCorte?: string): Promise<AntiguedadSaldos> => {
    const response = await api.get<AntiguedadSaldos>('/api/reportes/antiguedad-saldos', {
      params: { fechaCorte }
    });
    return response.data;
  },

  exportAntiguedadSaldosExcel: async (fechaCorte?: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/antiguedad-saldos/excel', {
      params: { fechaCorte },
      responseType: 'blob'
    });
    return response.data;
  },

  exportAntiguedadSaldosCsv: async (fechaCorte?: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/antiguedad-saldos/csv', {
      params: { fechaCorte },
      responseType: 'blob'
    });
    return response.data;
  },

  // Cartera Vencida
  getCarteraVencida: async (fechaCorte?: string): Promise<ReporteCarteraVencida> => {
    const response = await api.get<ReporteCarteraVencida>('/api/reportes/cartera-vencida', {
      params: { fechaCorte }
    });
    return response.data;
  },

  exportCarteraVencidaExcel: async (fechaCorte?: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/cartera-vencida/excel', {
      params: { fechaCorte },
      responseType: 'blob'
    });
    return response.data;
  },

  exportCarteraVencidaCsv: async (fechaCorte?: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/cartera-vencida/csv', {
      params: { fechaCorte },
      responseType: 'blob'
    });
    return response.data;
  },

  // Proyeccion de Cobranza
  getProyeccion: async (
    periodoInicio: string,
    periodoFin: string
  ): Promise<ProyeccionCobranzaReporte> => {
    const response = await api.get<ProyeccionCobranzaReporte>('/api/reportes/proyeccion', {
      params: { periodoInicio, periodoFin }
    });
    return response.data;
  },

  exportProyeccionExcel: async (periodoInicio: string, periodoFin: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/proyeccion/excel', {
      params: { periodoInicio, periodoFin },
      responseType: 'blob'
    });
    return response.data;
  },

  exportProyeccionCsv: async (periodoInicio: string, periodoFin: string): Promise<Blob> => {
    const response = await api.get('/api/reportes/proyeccion/csv', {
      params: { periodoInicio, periodoFin },
      responseType: 'blob'
    });
    return response.data;
  }
};

// Helper function to download blob
export const downloadBlob = (blob: Blob, filename: string) => {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

import api from './api';
import type {
  EstadoCuenta,
  AntiguedadSaldos,
  ReporteCarteraVencida,
  ProyeccionCobranzaReporte,
  Finiquito,
  ReporteMensual,
  EstadoCuentaMensual
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
    periodoFin: string,
    propiedadId?: number,
    arrendatarioId?: number,
    estadoContrato?: string
  ): Promise<ProyeccionCobranzaReporte> => {
    const response = await api.get<ProyeccionCobranzaReporte>('/api/reportes/proyeccion', {
      params: { periodoInicio, periodoFin, propiedadId, arrendatarioId, estadoContrato }
    });
    return response.data;
  },

  exportProyeccionExcel: async (
    periodoInicio: string,
    periodoFin: string,
    propiedadId?: number,
    arrendatarioId?: number,
    estadoContrato?: string
  ): Promise<Blob> => {
    const response = await api.get('/api/reportes/proyeccion/excel', {
      params: { periodoInicio, periodoFin, propiedadId, arrendatarioId, estadoContrato },
      responseType: 'blob'
    });
    return response.data;
  },

  exportProyeccionCsv: async (
    periodoInicio: string,
    periodoFin: string,
    propiedadId?: number,
    arrendatarioId?: number,
    estadoContrato?: string
  ): Promise<Blob> => {
    const response = await api.get('/api/reportes/proyeccion/csv', {
      params: { periodoInicio, periodoFin, propiedadId, arrendatarioId, estadoContrato },
      responseType: 'blob'
    });
    return response.data;
  },

  // Finiquito de Contrato
  getFiniquito: async (contratoId: number): Promise<Finiquito> => {
    const response = await api.get<Finiquito>(`/api/reportes/finiquito/${contratoId}`);
    return response.data;
  },

  exportFiniquitoExcel: async (contratoId: number): Promise<Blob> => {
    const response = await api.get(`/api/reportes/finiquito/${contratoId}/excel`, {
      responseType: 'blob'
    });
    return response.data;
  },

  exportFiniquitoCsv: async (contratoId: number): Promise<Blob> => {
    const response = await api.get(`/api/reportes/finiquito/${contratoId}/csv`, {
      responseType: 'blob'
    });
    return response.data;
  },

  // Reporte Mensual
  getReporteMensual: async (mes: number, anio: number): Promise<ReporteMensual> => {
    const response = await api.get<ReporteMensual>('/api/reportes/mensual', {
      params: { mes, anio }
    });
    return response.data;
  },

  exportReporteMensualExcel: async (mes: number, anio: number): Promise<Blob> => {
    const response = await api.get('/api/reportes/mensual/excel', {
      params: { mes, anio },
      responseType: 'blob'
    });
    return response.data;
  },

  exportReporteMensualCsv: async (mes: number, anio: number): Promise<Blob> => {
    const response = await api.get('/api/reportes/mensual/csv', {
      params: { mes, anio },
      responseType: 'blob'
    });
    return response.data;
  },

  // Estado de Cuenta Mensual por Cliente
  getEstadoCuentaMensual: async (
    personaId: number,
    mes: number,
    anio: number
  ): Promise<EstadoCuentaMensual> => {
    const response = await api.get<EstadoCuentaMensual>(`/api/reportes/estado-cuenta-mensual/${personaId}`, {
      params: { mes, anio }
    });
    return response.data;
  },

  exportEstadoCuentaMensualExcel: async (
    personaId: number,
    mes: number,
    anio: number
  ): Promise<Blob> => {
    const response = await api.get(`/api/reportes/estado-cuenta-mensual/${personaId}/excel`, {
      params: { mes, anio },
      responseType: 'blob'
    });
    return response.data;
  },

  exportEstadoCuentaMensualCsv: async (
    personaId: number,
    mes: number,
    anio: number
  ): Promise<Blob> => {
    const response = await api.get(`/api/reportes/estado-cuenta-mensual/${personaId}/csv`, {
      params: { mes, anio },
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

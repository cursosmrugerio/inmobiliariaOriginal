import api from './api';

export type TipoNotificacion = 'EMAIL' | 'WHATSAPP' | 'SMS';
export type CategoriaNotificacion = 'VENCIMIENTO_CONTRATO' | 'PAGO_PENDIENTE' | 'PAGO_VENCIDO' | 'CONFIRMACION_PAGO' | 'RECORDATORIO_GENERAL' | 'ALERTA_MOROSIDAD';
export type EstadoNotificacion = 'PENDIENTE' | 'ENVIADA' | 'FALLIDA' | 'CANCELADA';

export interface Notificacion {
  id: number;
  personaId?: number;
  personaNombre?: string;
  tipo: TipoNotificacion;
  categoria: CategoriaNotificacion;
  estado: EstadoNotificacion;
  destinatario: string;
  asunto: string;
  mensaje: string;
  referenciaId?: number;
  referenciaTipo?: string;
  fechaProgramada?: string;
  fechaEnvio?: string;
  intentos: number;
  errorMensaje?: string;
  fechaCreacion: string;
}

export interface ConfiguracionNotificacion {
  id: number;
  categoria: CategoriaNotificacion;
  emailHabilitado: boolean;
  whatsappHabilitado: boolean;
  diasAnticipacion: number;
  frecuenciaRecordatorio: number;
  maxIntentos: number;
  plantillaEmail?: string;
  plantillaWhatsapp?: string;
  activo: boolean;
}

export interface CreateNotificacionRequest {
  personaId?: number;
  tipo: TipoNotificacion;
  categoria: CategoriaNotificacion;
  destinatario: string;
  asunto: string;
  mensaje: string;
  referenciaId?: number;
  referenciaTipo?: string;
  fechaProgramada?: string;
}

export interface UpdateConfiguracionRequest {
  categoria: CategoriaNotificacion;
  emailHabilitado?: boolean;
  whatsappHabilitado?: boolean;
  diasAnticipacion?: number;
  frecuenciaRecordatorio?: number;
  maxIntentos?: number;
  plantillaEmail?: string;
  plantillaWhatsapp?: string;
  activo?: boolean;
}

export const notificacionService = {
  getAll: async (): Promise<Notificacion[]> => {
    const response = await api.get('/notificaciones');
    return response.data;
  },

  getById: async (id: number): Promise<Notificacion> => {
    const response = await api.get(`/notificaciones/${id}`);
    return response.data;
  },

  getByPersona: async (personaId: number): Promise<Notificacion[]> => {
    const response = await api.get(`/notificaciones/persona/${personaId}`);
    return response.data;
  },

  getByEstado: async (estado: EstadoNotificacion): Promise<Notificacion[]> => {
    const response = await api.get(`/notificaciones/estado/${estado}`);
    return response.data;
  },

  create: async (data: CreateNotificacionRequest): Promise<Notificacion> => {
    const response = await api.post('/notificaciones', data);
    return response.data;
  },

  enviar: async (id: number): Promise<Notificacion> => {
    const response = await api.post(`/notificaciones/${id}/enviar`);
    return response.data;
  },

  cancelar: async (id: number): Promise<void> => {
    await api.post(`/notificaciones/${id}/cancelar`);
  },

  // Configuración
  getConfiguraciones: async (): Promise<ConfiguracionNotificacion[]> => {
    const response = await api.get('/notificaciones/configuracion');
    return response.data;
  },

  getConfiguracion: async (categoria: CategoriaNotificacion): Promise<ConfiguracionNotificacion> => {
    const response = await api.get(`/notificaciones/configuracion/${categoria}`);
    return response.data;
  },

  updateConfiguracion: async (data: UpdateConfiguracionRequest): Promise<ConfiguracionNotificacion> => {
    const response = await api.put('/notificaciones/configuracion', data);
    return response.data;
  }
};

export default notificacionService;

export interface CarteraVencida {
  id: number;
  empresaId: number;
  contratoId: number;
  personaId: number;
  propiedadId: number;
  montoOriginal: number;
  montoPendiente: number;
  montoPenalidad: number;
  fechaVencimiento: string;
  diasVencido: number;
  concepto: string;
  estadoCobranza: EstadoCobranza;
  clasificacionAntiguedad: ClasificacionAntiguedad;
  porcentajePenalidad: number;
  activo: boolean;
  createdAt: string;
  updatedAt: string;
  nombrePersona?: string;
  direccionPropiedad?: string;
  montoTotal: number;
}

export type EstadoCobranza =
  | 'PENDIENTE'
  | 'EN_GESTION'
  | 'PROMESA_PAGO'
  | 'PARCIALMENTE_PAGADO'
  | 'PAGADO'
  | 'INCOBRABLE';

export type ClasificacionAntiguedad =
  | 'VIGENTE'
  | 'VENCIDO_1_30'
  | 'VENCIDO_31_60'
  | 'VENCIDO_61_90'
  | 'VENCIDO_MAS_90';

export interface SeguimientoCobranza {
  id: number;
  empresaId: number;
  carteraVencidaId: number;
  tipoContacto: TipoContacto;
  fechaContacto: string;
  descripcion: string;
  resultado: ResultadoContacto;
  fechaPromesaPago?: string;
  montoPromesa?: number;
  usuarioId?: number;
  proximaAccion?: string;
  fechaProximaAccion?: string;
  activo: boolean;
  createdAt: string;
  updatedAt: string;
  nombreUsuario?: string;
}

export type TipoContacto =
  | 'LLAMADA_TELEFONICA'
  | 'WHATSAPP'
  | 'EMAIL'
  | 'VISITA_DOMICILIO'
  | 'CARTA_COBRANZA'
  | 'NOTIFICACION_LEGAL';

export type ResultadoContacto =
  | 'CONTACTADO_PROMESA_PAGO'
  | 'CONTACTADO_SIN_COMPROMISO'
  | 'NO_CONTACTADO'
  | 'NUMERO_EQUIVOCADO'
  | 'BUZON_VOZ'
  | 'PAGO_REALIZADO';

export interface ProyeccionCobranza {
  id: number;
  empresaId: number;
  periodo: string;
  montoProyectado: number;
  montoCobrado: number;
  montoPendiente: number;
  cantidadContratos: number;
  cantidadPagosEsperados: number;
  cantidadPagosRecibidos: number;
  porcentajeCumplimiento: number;
  notas?: string;
  activo: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ResumenCobranza {
  totalCarteraVencida: number;
  totalPenalidades: number;
  totalGeneral: number;
  cantidadCuentasVencidas: number;
  montoVigente: number;
  monto1a30: number;
  monto31a60: number;
  monto61a90: number;
  montoMas90: number;
  cantidadVigente: number;
  cantidad1a30: number;
  cantidad31a60: number;
  cantidad61a90: number;
  cantidadMas90: number;
  pendientes: number;
  enGestion: number;
  promesasPago: number;
  parcialmentePagados: number;
}

export interface CreateCarteraVencidaRequest {
  contratoId: number;
  personaId: number;
  propiedadId: number;
  montoOriginal: number;
  fechaVencimiento: string;
  concepto?: string;
  porcentajePenalidad?: number;
}

export interface CreateSeguimientoRequest {
  carteraVencidaId: number;
  tipoContacto: TipoContacto;
  fechaContacto?: string;
  descripcion?: string;
  resultado?: ResultadoContacto;
  fechaPromesaPago?: string;
  montoPromesa?: number;
  proximaAccion?: string;
  fechaProximaAccion?: string;
}

export const estadoCobranzaLabels: Record<EstadoCobranza, string> = {
  PENDIENTE: 'Pendiente',
  EN_GESTION: 'En Gestión',
  PROMESA_PAGO: 'Promesa de Pago',
  PARCIALMENTE_PAGADO: 'Parcialmente Pagado',
  PAGADO: 'Pagado',
  INCOBRABLE: 'Incobrable'
};

export const clasificacionLabels: Record<ClasificacionAntiguedad, string> = {
  VIGENTE: 'Vigente',
  VENCIDO_1_30: '1-30 días',
  VENCIDO_31_60: '31-60 días',
  VENCIDO_61_90: '61-90 días',
  VENCIDO_MAS_90: '+90 días'
};

export const tipoContactoLabels: Record<TipoContacto, string> = {
  LLAMADA_TELEFONICA: 'Llamada Telefónica',
  WHATSAPP: 'WhatsApp',
  EMAIL: 'Email',
  VISITA_DOMICILIO: 'Visita a Domicilio',
  CARTA_COBRANZA: 'Carta de Cobranza',
  NOTIFICACION_LEGAL: 'Notificación Legal'
};

export const resultadoContactoLabels: Record<ResultadoContacto, string> = {
  CONTACTADO_PROMESA_PAGO: 'Contactado - Promesa de Pago',
  CONTACTADO_SIN_COMPROMISO: 'Contactado - Sin Compromiso',
  NO_CONTACTADO: 'No Contactado',
  NUMERO_EQUIVOCADO: 'Número Equivocado',
  BUZON_VOZ: 'Buzón de Voz',
  PAGO_REALIZADO: 'Pago Realizado'
};

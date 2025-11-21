export type EstadoContrato =
  | 'BORRADOR'
  | 'ACTIVO'
  | 'POR_VENCER'
  | 'VENCIDO'
  | 'RENOVADO'
  | 'TERMINADO'
  | 'CANCELADO';

export interface Contrato {
  id: number;
  numeroContrato: string;

  // Propiedad
  propiedadId: number;
  propiedadNombre: string;
  propiedadDireccion?: string;

  // Arrendatario
  arrendatarioId: number;
  arrendatarioNombre: string;
  arrendatarioEmail?: string;
  arrendatarioTelefono?: string;

  // Aval
  avalId?: number;
  avalNombre?: string;
  avalTelefono?: string;

  // Fechas
  fechaInicio: string;
  fechaFin: string;
  diaPago: number;

  // Montos
  montoRenta: number;
  montoDeposito?: number;
  montoPenalidadDiaria?: number;
  diasGracia?: number;
  porcentajeIncrementoAnual?: number;

  // Estado
  estado: EstadoContrato;
  condiciones?: string;
  notas?: string;
  contratoAnteriorId?: number;
  activo: boolean;

  // Calculados
  diasRestantes: number;
  vigente: boolean;
  porVencer: boolean;

  createdAt?: string;
  updatedAt?: string;
}

export interface CreateContratoRequest {
  numeroContrato?: string;
  propiedadId: number;
  arrendatarioId: number;
  avalId?: number;
  fechaInicio: string;
  fechaFin: string;
  diaPago: number;
  montoRenta: number;
  montoDeposito?: number;
  montoPenalidadDiaria?: number;
  diasGracia?: number;
  porcentajeIncrementoAnual?: number;
  condiciones?: string;
  notas?: string;
}

export interface UpdateContratoRequest {
  numeroContrato?: string;
  propiedadId?: number;
  arrendatarioId?: number;
  avalId?: number;
  fechaInicio?: string;
  fechaFin?: string;
  diaPago?: number;
  montoRenta?: number;
  montoDeposito?: number;
  montoPenalidadDiaria?: number;
  diasGracia?: number;
  porcentajeIncrementoAnual?: number;
  estado?: EstadoContrato;
  condiciones?: string;
  notas?: string;
  activo?: boolean;
}

export interface RenovarContratoRequest {
  nuevaFechaFin: string;
  nuevoMontoRenta?: number;
  nuevoAvalId?: number;
  nuevasCondiciones?: string;
  notas?: string;
  aplicarIncrementoAnual?: boolean;
}

export interface ContratoStats {
  activos: number;
  porVencer: number;
  vencidos: number;
  borradores: number;
}

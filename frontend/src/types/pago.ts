export type TipoPago = 'EFECTIVO' | 'TRANSFERENCIA' | 'CHEQUE' | 'TARJETA_DEBITO' | 'TARJETA_CREDITO' | 'DEPOSITO_BANCARIO';

export type EstadoPago = 'PENDIENTE' | 'APLICADO' | 'PARCIAL' | 'RECHAZADO' | 'CANCELADO';

export type TipoCargo = 'RENTA' | 'DEPOSITO' | 'PENALIDAD' | 'MANTENIMIENTO' | 'SERVICIO' | 'OTRO';

export type EstadoCargo = 'PENDIENTE' | 'PARCIAL' | 'PAGADO' | 'CANCELADO' | 'VENCIDO';

export interface PagoAplicacion {
  id: number;
  pagoId: number;
  cargoId: number;
  cargoConcepto: string;
  montoAplicado: number;
  createdAt: string;
}

export interface Pago {
  id: number;
  contratoId: number;
  numeroContrato: string;
  personaId: number;
  personaNombre: string;
  propiedadDireccion: string;
  numeroRecibo: string;
  monto: number;
  montoAplicado: number;
  montoDisponible: number;
  tipoPago: TipoPago;
  estado: EstadoPago;
  fechaPago: string;
  fechaAplicacion?: string;
  referencia?: string;
  banco?: string;
  numeroCheque?: string;
  notas?: string;
  comprobanteUrl?: string;
  aplicaciones: PagoAplicacion[];
  createdAt: string;
}

export interface CreatePagoRequest {
  contratoId: number;
  personaId: number;
  monto: number;
  tipoPago: TipoPago;
  fechaPago: string;
  referencia?: string;
  banco?: string;
  numeroCheque?: string;
  notas?: string;
  comprobanteUrl?: string;
  cargoIds?: number[];
  aplicarAutomaticamente?: boolean;
}

export interface AplicarPagoRequest {
  cargoId: number;
  montoAplicar: number;
}

export interface Cargo {
  id: number;
  contratoId: number;
  numeroContrato: string;
  propiedadDireccion: string;
  arrendatarioNombre: string;
  tipoCargo: TipoCargo;
  concepto: string;
  montoOriginal: number;
  montoPagado: number;
  montoPendiente: number;
  fechaCargo: string;
  fechaVencimiento: string;
  estado: EstadoCargo;
  esCargoFijo: boolean;
  periodoMes?: number;
  periodoAnio?: number;
  notas?: string;
  createdAt: string;
}

export interface CreateCargoRequest {
  contratoId: number;
  tipoCargo: TipoCargo;
  concepto: string;
  montoOriginal: number;
  fechaCargo: string;
  fechaVencimiento: string;
  esCargoFijo?: boolean;
  periodoMes?: number;
  periodoAnio?: number;
  notas?: string;
}

export interface GenerarCargosFijosRequest {
  mes: number;
  anio: number;
  contratoId?: number;
}

export interface PagoEstadisticas {
  totalPagadoMes: number;
  totalPagosMes: number;
  totalPendiente: number;
  totalCargosPendientes: number;
  totalCargosVencidos: number;
}

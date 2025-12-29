export interface EstadoCuentaItem {
  fecha: string;
  concepto: string;
  tipo: string;
  cargo: number;
  abono: number;
  saldo: number;
  referencia: string;
  diasVencido: number;
  estado: string;
}

export interface EstadoCuenta {
  personaId: number;
  nombreCliente: string;
  tipoPersona: string;
  rfc: string;
  email: string;
  telefono: string;
  direccion: string;
  empresaId: number;
  nombreEmpresa: string;
  fechaInicio: string;
  fechaFin: string;
  fechaGeneracion: string;
  saldoAnterior: number;
  totalCargos: number;
  totalAbonos: number;
  saldoActual: number;
  saldoVencido: number;
  saldoPorVencer: number;
  movimientos: EstadoCuentaItem[];
  propiedades: string[];
}

export interface AntiguedadSaldosItem {
  personaId: number;
  nombreCliente: string;
  propiedadId: number;
  direccionPropiedad: string;
  vigente: number;
  vencido1a30: number;
  vencido31a60: number;
  vencido61a90: number;
  vencidoMas90: number;
  totalVencido: number;
  saldoTotal: number;
  cantidadDocumentos: number;
}

export interface AntiguedadSaldos {
  empresaId: number;
  nombreEmpresa: string;
  fechaCorte: string;
  fechaGeneracion: string;
  totalVigente: number;
  totalVencido1a30: number;
  totalVencido31a60: number;
  totalVencido61a90: number;
  totalVencidoMas90: number;
  totalVencido: number;
  totalGeneral: number;
  cantidadClientes: number;
  cantidadDocumentos: number;
  porcentajeVigente: number;
  porcentajeVencido: number;
  detalle: AntiguedadSaldosItem[];
}

export interface CarteraVencidaItem {
  id: number;
  personaId: number;
  nombreCliente: string;
  propiedadId: number;
  direccionPropiedad: string;
  concepto: string;
  fechaVencimiento: string;
  diasVencido: number;
  clasificacion: string;
  montoOriginal: number;
  montoPendiente: number;
  montoPenalidad: number;
  montoTotal: number;
  estadoCobranza: string;
  ultimaGestion: string;
  fechaUltimaGestion: string;
}

export interface ReporteCarteraVencida {
  empresaId: number;
  nombreEmpresa: string;
  fechaCorte: string;
  fechaGeneracion: string;
  totalCartera: number;
  totalPenalidades: number;
  totalGeneral: number;
  cantidadCuentas: number;
  cuentasPendientes: number;
  cuentasEnGestion: number;
  cuentasConPromesa: number;
  cuentasParcialmentePagadas: number;
  montoPendiente: number;
  montoEnGestion: number;
  montoConPromesa: number;
  montoParcialmentePagado: number;
  montoVigente: number;
  monto1a30: number;
  monto31a60: number;
  monto61a90: number;
  montoMas90: number;
  detalle: CarteraVencidaItem[];
}

export interface ProyeccionMes {
  periodo: string;
  mesAnio: string;
  montoProyectado: number;
  montoCobrado: number;
  diferencia: number;
  porcentajeCumplimiento: number;
  cantidadContratos: number;
  pagosEsperados: number;
  pagosRecibidos: number;
}

export interface ProyeccionCobranzaReporte {
  empresaId: number;
  nombreEmpresa: string;
  periodoInicio: string;
  periodoFin: string;
  fechaGeneracion: string;
  totalProyectado: number;
  totalCobrado: number;
  totalPendiente: number;
  porcentajeCumplimiento: number;
  totalContratosActivos: number;
  totalPagosEsperados: number;
  totalPagosRecibidos: number;
  detalleMensual: ProyeccionMes[];
}

// Finiquito de Contrato
export interface ConceptoFiniquito {
  concepto: string;
  tipo: string;
  fecha: string;
  monto: number;
  estado: string;
  notas?: string;
}

export interface Finiquito {
  contratoId: number;
  numeroContrato: string;
  empresaId: number;
  nombreEmpresa: string;
  arrendatarioId: number;
  nombreArrendatario: string;
  rfcArrendatario?: string;
  emailArrendatario?: string;
  telefonoArrendatario?: string;
  propiedadId: number;
  direccionPropiedad: string;
  tipoPropiedad: string;
  fechaInicioContrato: string;
  fechaFinContrato: string;
  fechaTerminacion?: string;
  motivoTerminacion?: string;
  montoRentaMensual: number;
  montoDeposito?: number;
  totalRentasPagadas: number;
  totalRentasPendientes: number;
  totalCargosAdicionales: number;
  totalPagosRealizados: number;
  saldoPendiente: number;
  depositoADevolver: number;
  deduccionesDeposito: number;
  montoLiquidacion: number;
  conceptos: ConceptoFiniquito[];
  fechaGeneracion: string;
  generadoPor?: string;
}

// Reporte Mensual
export interface PropiedadMensual {
  propiedadId: number;
  direccion: string;
  tipoPropiedad: string;
  estadoOcupacion: string;
  arrendatario?: string;
  rentaMensual: number;
  rentaCobrada: number;
  saldoPendiente: number;
  estadoPago: string;
}

export interface IngresoMensual {
  fecha: string;
  concepto: string;
  propiedad: string;
  cliente: string;
  monto: number;
  tipoPago: string;
  referencia?: string;
}

export interface Moroso {
  personaId: number;
  nombre: string;
  propiedad: string;
  montoAdeudado: number;
  diasVencido: number;
  estadoCobranza: string;
}

export interface ReporteMensual {
  empresaId: number;
  nombreEmpresa: string;
  mes: number;
  anio: number;
  periodoDescripcion: string;
  fechaGeneracion: string;
  totalPropiedades: number;
  propiedadesOcupadas: number;
  propiedadesDisponibles: number;
  porcentajeOcupacion: number;
  contratosActivos: number;
  contratosPorVencer: number;
  contratosVencidos: number;
  contratosNuevos: number;
  contratosTerminados: number;
  contratosRenovados: number;
  ingresosPorRenta: number;
  ingresosPorOtrosConceptos: number;
  totalIngresos: number;
  rentaEsperada: number;
  rentaCobrada: number;
  porcentajeCobranza: number;
  carteraVigente: number;
  carteraVencida: number;
  carteraTotal: number;
  clientesConAdeudo: number;
  clientesAlCorriente: number;
  detallePropiedades: PropiedadMensual[];
  detalleIngresos: IngresoMensual[];
  topMorosos: Moroso[];
}

// Estado de Cuenta Mensual por Cliente
export interface MovimientoMensual {
  fecha: string;
  concepto: string;
  tipo: string;
  cargo: number;
  abono: number;
  saldoAcumulado: number;
  referencia: string;
  propiedad: string;
  estado: string;
}

export interface PropiedadResumen {
  propiedadId: number;
  direccion: string;
  rentaMensual: number;
  cargosDelMes: number;
  pagosDelMes: number;
  saldoPendiente: number;
  estadoPago: string;
}

export interface EstadoCuentaMensual {
  personaId: number;
  nombreCliente: string;
  tipoPersona: string;
  rfc: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  empresaId: number;
  nombreEmpresa: string;
  mes: number;
  anio: number;
  periodoDescripcion: string;
  fechaGeneracion: string;
  saldoInicial: number;
  totalCargos: number;
  totalAbonos: number;
  saldoFinal: number;
  saldoVencido: number;
  saldoPorVencer: number;
  diasPromedioVencido: number;
  movimientos: MovimientoMensual[];
  propiedades: PropiedadResumen[];
}

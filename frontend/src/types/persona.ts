export type TipoPersona = 'FISICA' | 'MORAL';
export type TipoDireccion = 'FISCAL' | 'CORRESPONDENCIA' | 'OTRA';

export interface Persona {
  id: number;
  empresaId: number;
  tipoPersona: TipoPersona;
  nombre?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  fechaNacimiento?: string;
  curp?: string;
  razonSocial?: string;
  nombreComercial?: string;
  rfc?: string;
  email?: string;
  telefono?: string;
  telefonoMovil?: string;
  nombreCompleto: string;
  activo: boolean;
  createdAt?: string;
  updatedAt?: string;
  roles?: PersonaRol[];
  direcciones?: Direccion[];
  cuentasBancarias?: CuentaBancaria[];
}

export interface PersonaRol {
  id: number;
  rolId: number;
  rolNombre: string;
  fechaAsignacion: string;
  activo: boolean;
}

export interface Direccion {
  id: number;
  personaId: number;
  tipoDireccion: TipoDireccion;
  calle: string;
  numeroExterior?: string;
  numeroInterior?: string;
  estadoId?: number;
  estadoNombre?: string;
  municipioId?: number;
  municipioNombre?: string;
  coloniaId?: number;
  coloniaNombre?: string;
  codigoPostal?: string;
  referencias?: string;
  esPrincipal: boolean;
  activo: boolean;
}

export interface CuentaBancaria {
  id: number;
  personaId: number;
  banco: string;
  numeroCuenta?: string;
  clabe?: string;
  titular?: string;
  esPrincipal: boolean;
  activo: boolean;
}

export interface CreatePersonaRequest {
  tipoPersona: TipoPersona;
  nombre?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  fechaNacimiento?: string;
  curp?: string;
  razonSocial?: string;
  nombreComercial?: string;
  rfc?: string;
  email?: string;
  telefono?: string;
  telefonoMovil?: string;
  rolesIds?: number[];
}

export interface UpdatePersonaRequest {
  tipoPersona?: TipoPersona;
  nombre?: string;
  apellidoPaterno?: string;
  apellidoMaterno?: string;
  fechaNacimiento?: string;
  curp?: string;
  razonSocial?: string;
  nombreComercial?: string;
  rfc?: string;
  email?: string;
  telefono?: string;
  telefonoMovil?: string;
  activo?: boolean;
}

export interface CreateDireccionRequest {
  tipoDireccion?: TipoDireccion;
  calle: string;
  numeroExterior?: string;
  numeroInterior?: string;
  estadoId?: number;
  municipioId?: number;
  coloniaId?: number;
  codigoPostal?: string;
  referencias?: string;
  esPrincipal?: boolean;
}

export interface CreateCuentaBancariaRequest {
  banco: string;
  numeroCuenta?: string;
  clabe?: string;
  titular?: string;
  esPrincipal?: boolean;
}

export interface TipoPropiedad {
  id: number;
  nombre: string;
  descripcion?: string;
  activo: boolean;
}

export interface PropiedadPropietario {
  id: number;
  propiedadId: number;
  propietarioId: number;
  propietarioNombre: string;
  propietarioRfc?: string;
  porcentajePropiedad: number;
  fechaAdquisicion?: string;
  esPrincipal: boolean;
  activo: boolean;
}

export interface Propiedad {
  id: number;
  empresaId: number;

  // Tipo
  tipoPropiedadId: number;
  tipoPropiedadNombre?: string;

  // Identificación
  nombre: string;
  claveCatastral?: string;

  // Dirección
  calle?: string;
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
  direccionCompleta?: string;

  // Características
  superficieTerreno?: number;
  superficieConstruccion?: number;
  numRecamaras?: number;
  numBanos?: number;
  numEstacionamientos?: number;
  numPisos?: number;
  anioConstruccion?: number;

  // Valores
  valorComercial?: number;
  valorCatastral?: number;
  rentaMensual?: number;

  // Estado
  disponible: boolean;
  notas?: string;
  activo: boolean;

  createdAt?: string;
  updatedAt?: string;

  // Relaciones
  propietarios?: PropiedadPropietario[];
}

export interface CreatePropiedadRequest {
  tipoPropiedadId: number;
  nombre: string;
  claveCatastral?: string;
  calle: string;
  numeroExterior?: string;
  numeroInterior?: string;
  estadoId?: number;
  municipioId?: number;
  coloniaId?: number;
  codigoPostal?: string;
  referencias?: string;
  superficieTerreno?: number;
  superficieConstruccion?: number;
  numRecamaras?: number;
  numBanos?: number;
  numEstacionamientos?: number;
  numPisos?: number;
  anioConstruccion?: number;
  valorComercial?: number;
  valorCatastral?: number;
  rentaMensual?: number;
  notas?: string;
  propietariosIds?: number[];
}

export interface UpdatePropiedadRequest {
  tipoPropiedadId?: number;
  nombre?: string;
  claveCatastral?: string;
  calle?: string;
  numeroExterior?: string;
  numeroInterior?: string;
  estadoId?: number;
  municipioId?: number;
  coloniaId?: number;
  codigoPostal?: string;
  referencias?: string;
  superficieTerreno?: number;
  superficieConstruccion?: number;
  numRecamaras?: number;
  numBanos?: number;
  numEstacionamientos?: number;
  numPisos?: number;
  anioConstruccion?: number;
  valorComercial?: number;
  valorCatastral?: number;
  rentaMensual?: number;
  disponible?: boolean;
  notas?: string;
  activo?: boolean;
}

export interface AddPropietarioRequest {
  propietarioId: number;
  porcentajePropiedad?: number;
  fechaAdquisicion?: string;
  esPrincipal?: boolean;
}

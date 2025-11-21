import api from './api';

export type CategoriaMantenimiento =
  | 'PLOMERIA'
  | 'ELECTRICIDAD'
  | 'CARPINTERIA'
  | 'PINTURA'
  | 'LIMPIEZA'
  | 'JARDINERIA'
  | 'CERRAJERIA'
  | 'AIRE_ACONDICIONADO'
  | 'OTROS';

export type PrioridadOrden = 'BAJA' | 'MEDIA' | 'ALTA' | 'URGENTE';

export type EstadoOrden = 'PENDIENTE' | 'EN_PROCESO' | 'COMPLETADA' | 'CANCELADA';

export interface Proveedor {
  id: number;
  empresaId: number;
  nombre: string;
  razonSocial?: string;
  rfc?: string;
  telefonoPrincipal?: string;
  telefonoSecundario?: string;
  email?: string;
  direccion?: string;
  codigoPostal?: string;
  ciudad?: string;
  estado?: string;
  nombreContacto?: string;
  categorias: CategoriaMantenimiento[];
  notas?: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion?: string;
}

export interface CreateProveedorRequest {
  nombre: string;
  razonSocial?: string;
  rfc?: string;
  telefonoPrincipal?: string;
  telefonoSecundario?: string;
  email?: string;
  direccion?: string;
  codigoPostal?: string;
  ciudad?: string;
  estado?: string;
  nombreContacto?: string;
  categorias?: CategoriaMantenimiento[];
  notas?: string;
}

export interface UpdateProveedorRequest extends CreateProveedorRequest {
  activo?: boolean;
}

export interface OrdenMantenimiento {
  id: number;
  empresaId: number;
  numeroOrden: string;
  propiedadId: number;
  propiedadNombre?: string;
  proveedorId?: number;
  proveedorNombre?: string;
  solicitanteId?: number;
  solicitanteNombre?: string;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMantenimiento;
  prioridad: PrioridadOrden;
  estado: EstadoOrden;
  fechaSolicitud: string;
  fechaProgramada?: string;
  fechaInicio?: string;
  fechaCompletada?: string;
  costoEstimado?: number;
  costoFinal?: number;
  notasTecnicas?: string;
  notasCierre?: string;
  fechaCreacion: string;
  fechaActualizacion?: string;
  creadoPor?: string;
}

export interface CreateOrdenRequest {
  propiedadId: number;
  proveedorId?: number;
  solicitanteId?: number;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMantenimiento;
  prioridad: PrioridadOrden;
  fechaProgramada?: string;
  costoEstimado?: number;
  notasTecnicas?: string;
}

export interface UpdateOrdenRequest {
  proveedorId?: number;
  solicitanteId?: number;
  titulo: string;
  descripcion: string;
  categoria: CategoriaMantenimiento;
  prioridad: PrioridadOrden;
  estado?: EstadoOrden;
  fechaProgramada?: string;
  fechaInicio?: string;
  fechaCompletada?: string;
  costoEstimado?: number;
  costoFinal?: number;
  notasTecnicas?: string;
  notasCierre?: string;
}

export interface CambiarEstadoRequest {
  estado: EstadoOrden;
  comentario?: string;
  costoFinal?: number;
  notasCierre?: string;
}

export interface SeguimientoOrden {
  id: number;
  ordenId: number;
  estadoAnterior?: EstadoOrden;
  estadoNuevo: EstadoOrden;
  comentario?: string;
  usuario?: string;
  fechaRegistro: string;
}

export interface EstadisticasMantenimiento {
  proveedoresActivos: number;
  ordenesPendientes: number;
  ordenesEnProceso: number;
  ordenesCompletadas: number;
  costosMesActual: number;
}

export const mantenimientoService = {
  // ==================== PROVEEDORES ====================

  getAllProveedores: async (): Promise<Proveedor[]> => {
    const response = await api.get('/mantenimiento/proveedores');
    return response.data;
  },

  getProveedoresActivos: async (): Promise<Proveedor[]> => {
    const response = await api.get('/mantenimiento/proveedores/activos');
    return response.data;
  },

  getProveedorById: async (id: number): Promise<Proveedor> => {
    const response = await api.get(`/mantenimiento/proveedores/${id}`);
    return response.data;
  },

  getProveedoresByCategoria: async (categoria: CategoriaMantenimiento): Promise<Proveedor[]> => {
    const response = await api.get(`/mantenimiento/proveedores/categoria/${categoria}`);
    return response.data;
  },

  createProveedor: async (data: CreateProveedorRequest): Promise<Proveedor> => {
    const response = await api.post('/mantenimiento/proveedores', data);
    return response.data;
  },

  updateProveedor: async (id: number, data: UpdateProveedorRequest): Promise<Proveedor> => {
    const response = await api.put(`/mantenimiento/proveedores/${id}`, data);
    return response.data;
  },

  deleteProveedor: async (id: number): Promise<void> => {
    await api.delete(`/mantenimiento/proveedores/${id}`);
  },

  // ==================== ÓRDENES ====================

  getAllOrdenes: async (): Promise<OrdenMantenimiento[]> => {
    const response = await api.get('/mantenimiento/ordenes');
    return response.data;
  },

  getOrdenesActivas: async (): Promise<OrdenMantenimiento[]> => {
    const response = await api.get('/mantenimiento/ordenes/activas');
    return response.data;
  },

  getOrdenById: async (id: number): Promise<OrdenMantenimiento> => {
    const response = await api.get(`/mantenimiento/ordenes/${id}`);
    return response.data;
  },

  getOrdenesByPropiedad: async (propiedadId: number): Promise<OrdenMantenimiento[]> => {
    const response = await api.get(`/mantenimiento/ordenes/propiedad/${propiedadId}`);
    return response.data;
  },

  getOrdenesByProveedor: async (proveedorId: number): Promise<OrdenMantenimiento[]> => {
    const response = await api.get(`/mantenimiento/ordenes/proveedor/${proveedorId}`);
    return response.data;
  },

  getOrdenesByEstado: async (estado: EstadoOrden): Promise<OrdenMantenimiento[]> => {
    const response = await api.get(`/mantenimiento/ordenes/estado/${estado}`);
    return response.data;
  },

  getOrdenesProgramadas: async (inicio: string, fin: string): Promise<OrdenMantenimiento[]> => {
    const response = await api.get(`/mantenimiento/ordenes/programadas?inicio=${inicio}&fin=${fin}`);
    return response.data;
  },

  createOrden: async (data: CreateOrdenRequest): Promise<OrdenMantenimiento> => {
    const response = await api.post('/mantenimiento/ordenes', data);
    return response.data;
  },

  updateOrden: async (id: number, data: UpdateOrdenRequest): Promise<OrdenMantenimiento> => {
    const response = await api.put(`/mantenimiento/ordenes/${id}`, data);
    return response.data;
  },

  cambiarEstadoOrden: async (id: number, data: CambiarEstadoRequest): Promise<OrdenMantenimiento> => {
    const response = await api.patch(`/mantenimiento/ordenes/${id}/estado`, data);
    return response.data;
  },

  deleteOrden: async (id: number): Promise<void> => {
    await api.delete(`/mantenimiento/ordenes/${id}`);
  },

  // ==================== SEGUIMIENTO ====================

  getSeguimientoByOrden: async (ordenId: number): Promise<SeguimientoOrden[]> => {
    const response = await api.get(`/mantenimiento/ordenes/${ordenId}/seguimiento`);
    return response.data;
  },

  // ==================== ESTADÍSTICAS ====================

  getEstadisticas: async (): Promise<EstadisticasMantenimiento> => {
    const response = await api.get('/mantenimiento/estadisticas');
    return response.data;
  },

  // ==================== CATÁLOGOS ====================

  getCategorias: async (): Promise<CategoriaMantenimiento[]> => {
    const response = await api.get('/mantenimiento/categorias');
    return response.data;
  },

  getPrioridades: async (): Promise<PrioridadOrden[]> => {
    const response = await api.get('/mantenimiento/prioridades');
    return response.data;
  },

  getEstados: async (): Promise<EstadoOrden[]> => {
    const response = await api.get('/mantenimiento/estados');
    return response.data;
  },

  // ==================== HELPERS ====================

  getCategoriaLabel: (categoria: CategoriaMantenimiento): string => {
    const labels: Record<CategoriaMantenimiento, string> = {
      PLOMERIA: 'Plomeria',
      ELECTRICIDAD: 'Electricidad',
      CARPINTERIA: 'Carpinteria',
      PINTURA: 'Pintura',
      LIMPIEZA: 'Limpieza',
      JARDINERIA: 'Jardineria',
      CERRAJERIA: 'Cerrajeria',
      AIRE_ACONDICIONADO: 'Aire Acondicionado',
      OTROS: 'Otros',
    };
    return labels[categoria] || categoria;
  },

  getPrioridadLabel: (prioridad: PrioridadOrden): string => {
    const labels: Record<PrioridadOrden, string> = {
      BAJA: 'Baja',
      MEDIA: 'Media',
      ALTA: 'Alta',
      URGENTE: 'Urgente',
    };
    return labels[prioridad] || prioridad;
  },

  getEstadoLabel: (estado: EstadoOrden): string => {
    const labels: Record<EstadoOrden, string> = {
      PENDIENTE: 'Pendiente',
      EN_PROCESO: 'En Proceso',
      COMPLETADA: 'Completada',
      CANCELADA: 'Cancelada',
    };
    return labels[estado] || estado;
  },

  getPrioridadColor: (prioridad: PrioridadOrden): 'default' | 'info' | 'warning' | 'error' => {
    const colors: Record<PrioridadOrden, 'default' | 'info' | 'warning' | 'error'> = {
      BAJA: 'default',
      MEDIA: 'info',
      ALTA: 'warning',
      URGENTE: 'error',
    };
    return colors[prioridad] || 'default';
  },

  getEstadoColor: (estado: EstadoOrden): 'default' | 'primary' | 'success' | 'error' => {
    const colors: Record<EstadoOrden, 'default' | 'primary' | 'success' | 'error'> = {
      PENDIENTE: 'default',
      EN_PROCESO: 'primary',
      COMPLETADA: 'success',
      CANCELADA: 'error',
    };
    return colors[estado] || 'default';
  },
};

export default mantenimientoService;

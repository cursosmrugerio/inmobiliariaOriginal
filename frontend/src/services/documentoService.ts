import api from './api';

export type TipoDocumento = 'CONTRATO' | 'IDENTIFICACION' | 'COMPROBANTE_DOMICILIO' | 'COMPROBANTE_INGRESOS' | 'ESCRITURA' | 'RECIBO' | 'FACTURA' | 'FOTO' | 'PLANO' | 'AVALUO' | 'OTRO';
export type TipoEntidad = 'PERSONA' | 'PROPIEDAD' | 'CONTRATO' | 'PAGO' | 'EMPRESA';

export interface Documento {
  id: number;
  nombre: string;
  nombreOriginal: string;
  tipoDocumento: TipoDocumento;
  tipoEntidad: TipoEntidad;
  entidadId: number;
  contentType: string;
  tamano: number;
  descripcion?: string;
  fechaCreacion: string;
  creadoPor?: string;
}

export interface CreateDocumentoRequest {
  nombre: string;
  tipoDocumento: TipoDocumento;
  tipoEntidad: TipoEntidad;
  entidadId: number;
  descripcion?: string;
}

export interface UpdateDocumentoRequest {
  nombre: string;
  tipoDocumento?: TipoDocumento;
  descripcion?: string;
}

export const documentoService = {
  getAll: async (): Promise<Documento[]> => {
    const response = await api.get('/documentos');
    return response.data;
  },

  getById: async (id: number): Promise<Documento> => {
    const response = await api.get(`/documentos/${id}`);
    return response.data;
  },

  getByEntidad: async (tipoEntidad: TipoEntidad, entidadId: number): Promise<Documento[]> => {
    const response = await api.get(`/documentos/entidad/${tipoEntidad}/${entidadId}`);
    return response.data;
  },

  getByTipoDocumento: async (tipoDocumento: TipoDocumento): Promise<Documento[]> => {
    const response = await api.get(`/documentos/tipo/${tipoDocumento}`);
    return response.data;
  },

  upload: async (file: File, data: CreateDocumentoRequest): Promise<Documento> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('nombre', data.nombre);
    formData.append('tipoDocumento', data.tipoDocumento);
    formData.append('tipoEntidad', data.tipoEntidad);
    formData.append('entidadId', data.entidadId.toString());
    if (data.descripcion) {
      formData.append('descripcion', data.descripcion);
    }

    const response = await api.post('/documentos', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  update: async (id: number, data: UpdateDocumentoRequest): Promise<Documento> => {
    const response = await api.put(`/documentos/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/documentos/${id}`);
  },

  download: async (id: number): Promise<Blob> => {
    const response = await api.get(`/documentos/${id}/download`, {
      responseType: 'blob',
    });
    return response.data;
  },

  countByEntidad: async (tipoEntidad: TipoEntidad, entidadId: number): Promise<number> => {
    const response = await api.get(`/documentos/entidad/${tipoEntidad}/${entidadId}/count`);
    return response.data;
  },

  getStorageUsed: async (): Promise<number> => {
    const response = await api.get('/documentos/storage/used');
    return response.data;
  },

  // Helper function to format file size
  formatFileSize: (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  },

  // Helper to get document type label
  getTipoDocumentoLabel: (tipo: TipoDocumento): string => {
    const labels: Record<TipoDocumento, string> = {
      CONTRATO: 'Contrato',
      IDENTIFICACION: 'Identificacion',
      COMPROBANTE_DOMICILIO: 'Comprobante de Domicilio',
      COMPROBANTE_INGRESOS: 'Comprobante de Ingresos',
      ESCRITURA: 'Escritura',
      RECIBO: 'Recibo',
      FACTURA: 'Factura',
      FOTO: 'Foto',
      PLANO: 'Plano',
      AVALUO: 'Avaluo',
      OTRO: 'Otro',
    };
    return labels[tipo] || tipo;
  },

  // Helper to get entity type label
  getTipoEntidadLabel: (tipo: TipoEntidad): string => {
    const labels: Record<TipoEntidad, string> = {
      PERSONA: 'Persona',
      PROPIEDAD: 'Propiedad',
      CONTRATO: 'Contrato',
      PAGO: 'Pago',
      EMPRESA: 'Empresa',
    };
    return labels[tipo] || tipo;
  },
};

export default documentoService;

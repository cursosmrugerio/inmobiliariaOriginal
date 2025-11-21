import api from './api';

export type TipoDocumento = 'CONTRATO' | 'IDENTIFICACION' | 'COMPROBANTE_DOMICILIO' | 'COMPROBANTE_PAGO' | 'AVALUO' | 'ESCRITURA' | 'ACTA_CONSTITUTIVA' | 'PODER_NOTARIAL' | 'OTRO';
export type TipoEntidad = 'PERSONA' | 'PROPIEDAD' | 'CONTRATO' | 'PAGO';

export interface Documento {
  id: number;
  nombre: string;
  nombreOriginal?: string;
  tipoDocumento: TipoDocumento;
  tipoEntidad: TipoEntidad;
  entidadId: number;
  tipoMime?: string;
  tamanio?: number;
  descripcion?: string;
  fechaDocumento?: string;
  fechaVencimiento?: string;
  activo: boolean;
  fechaCreacion: string;
}

export interface UpdateDocumentoRequest {
  nombre?: string;
  tipoDocumento?: TipoDocumento;
  descripcion?: string;
  fechaDocumento?: string;
  fechaVencimiento?: string;
  activo?: boolean;
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

  getByTipo: async (tipoDocumento: TipoDocumento): Promise<Documento[]> => {
    const response = await api.get(`/documentos/tipo/${tipoDocumento}`);
    return response.data;
  },

  upload: async (
    file: File,
    nombre: string,
    tipoDocumento: TipoDocumento,
    tipoEntidad: TipoEntidad,
    entidadId: number,
    descripcion?: string
  ): Promise<Documento> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('nombre', nombre);
    formData.append('tipoDocumento', tipoDocumento);
    formData.append('tipoEntidad', tipoEntidad);
    formData.append('entidadId', entidadId.toString());
    if (descripcion) {
      formData.append('descripcion', descripcion);
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

  download: async (id: number, filename: string): Promise<void> => {
    const response = await api.get(`/documentos/${id}/download`, {
      responseType: 'blob',
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },
};

export default documentoService;

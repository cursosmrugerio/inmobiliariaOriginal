package com.inmobiliaria.documento.dto;

import com.inmobiliaria.documento.TipoDocumento;
import lombok.Data;

@Data
public class UpdateDocumentoRequest {
    private String nombre;
    private String descripcion;
    private TipoDocumento tipoDocumento;
    private String entidadTipo;
    private Long entidadId;
}

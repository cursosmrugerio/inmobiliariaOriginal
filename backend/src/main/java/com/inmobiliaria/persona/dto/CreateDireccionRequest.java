package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.TipoDireccion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDireccionRequest {

    private TipoDireccion tipoDireccion = TipoDireccion.FISCAL;

    @NotBlank(message = "La calle es requerida")
    @Size(max = 200, message = "La calle no puede exceder 200 caracteres")
    private String calle;

    @Size(max = 20, message = "El número exterior no puede exceder 20 caracteres")
    private String numeroExterior;

    @Size(max = 20, message = "El número interior no puede exceder 20 caracteres")
    private String numeroInterior;

    private Integer estadoId;
    private Integer municipioId;
    private Integer coloniaId;

    @Size(max = 5, message = "El código postal no puede exceder 5 caracteres")
    private String codigoPostal;

    private String referencias;
    private boolean esPrincipal = false;
}

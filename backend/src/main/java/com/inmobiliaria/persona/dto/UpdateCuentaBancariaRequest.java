package com.inmobiliaria.persona.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCuentaBancariaRequest {

    @Size(max = 100, message = "El banco no puede exceder 100 caracteres")
    private String banco;

    @Size(max = 20, message = "El número de cuenta no puede exceder 20 caracteres")
    private String numeroCuenta;

    @Size(max = 18, message = "La CLABE no puede exceder 18 caracteres")
    private String clabe;

    @Size(max = 200, message = "El titular no puede exceder 200 caracteres")
    private String titular;

    private Boolean esPrincipal;
    private Boolean activo;
}

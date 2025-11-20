package com.inmobiliaria.persona.dto;

import com.inmobiliaria.persona.CuentaBancaria;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CuentaBancariaDTO {
    private Long id;
    private Long personaId;
    private String banco;
    private String numeroCuenta;
    private String clabe;
    private String titular;
    private boolean esPrincipal;
    private boolean activo;

    public static CuentaBancariaDTO fromEntity(CuentaBancaria cuenta) {
        return CuentaBancariaDTO.builder()
                .id(cuenta.getId())
                .personaId(cuenta.getPersona().getId())
                .banco(cuenta.getBanco())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .clabe(cuenta.getClabe())
                .titular(cuenta.getTitular())
                .esPrincipal(cuenta.isEsPrincipal())
                .activo(cuenta.isActivo())
                .build();
    }
}

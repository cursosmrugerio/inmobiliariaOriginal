package com.inmobiliaria.shared.multitenancy;

public interface TenantAware {
    Long getEmpresaId();
    void setEmpresaId(Long empresaId);
}

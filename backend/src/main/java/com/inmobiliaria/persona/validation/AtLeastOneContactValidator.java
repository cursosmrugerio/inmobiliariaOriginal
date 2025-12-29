package com.inmobiliaria.persona.validation;

import com.inmobiliaria.persona.dto.CreatePersonaRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneContactValidator implements ConstraintValidator<AtLeastOneContact, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String email = null;
        String telefono = null;
        String telefonoMovil = null;

        if (value instanceof CreatePersonaRequest request) {
            email = request.getEmail();
            telefono = request.getTelefono();
            telefonoMovil = request.getTelefonoMovil();
        } else if (value instanceof com.inmobiliaria.persona.dto.UpdatePersonaRequest request) {
            email = request.getEmail();
            telefono = request.getTelefono();
            telefonoMovil = request.getTelefonoMovil();
        }

        return hasValue(email) || hasValue(telefono) || hasValue(telefonoMovil);
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

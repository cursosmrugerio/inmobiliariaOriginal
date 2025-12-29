package com.inmobiliaria.persona.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AtLeastOneContactValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneContact {
    String message() default "Debe proporcionar al menos un medio de contacto (email, teléfono o teléfono móvil)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

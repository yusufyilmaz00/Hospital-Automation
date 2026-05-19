package com.seproje.hospital.auth.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Şifre geçerli değil";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

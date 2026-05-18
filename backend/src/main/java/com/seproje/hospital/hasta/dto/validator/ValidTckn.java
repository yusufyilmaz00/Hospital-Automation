package com.seproje.hospital.hasta.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TcknValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTckn {

    String message() default "TCKN geçerli değil";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
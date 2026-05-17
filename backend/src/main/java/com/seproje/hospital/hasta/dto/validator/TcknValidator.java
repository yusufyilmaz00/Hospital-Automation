package com.seproje.hospital.hasta.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TcknValidator implements ConstraintValidator<ValidTckn, String> {

    @Override
    public boolean isValid(String tckn, ConstraintValidatorContext context) {

        if (tckn == null) {
            buildMessage(context, "TCKN boş olamaz");
            return false;
        }

        if (!tckn.matches("\\d{11}")) {
            buildMessage(context, "TCKN 11 haneli ve sadece rakamlardan oluşmalıdır");
            return false;
        }

        if (tckn.startsWith("0")) {
            buildMessage(context, "TCKN 0 ile başlayamaz");
            return false;
        }

        int sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(tckn.charAt(i));
        }

        int check = sum % 10;
        int lastDigit = Character.getNumericValue(tckn.charAt(10));

        if (check != lastDigit) {
            buildMessage(context, "TCKN checksum hatalı");
            return false;
        }

        return true;
    }

    private void buildMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
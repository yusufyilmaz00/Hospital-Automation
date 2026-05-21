package com.seproje.hospital.auth.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password == null || password.isBlank()) {
            buildMessage(context, "Şifre boş olamaz");
            return false;
        }

        if (password.length() < 8) {
            buildMessage(context, "Şifre en az 8 karakter olmalıdır");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            buildMessage(context, "Şifre en az bir büyük harf içermelidir");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            buildMessage(context, "Şifre en az bir küçük harf içermelidir");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            buildMessage(context, "Şifre en az bir rakam içermelidir");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            buildMessage(context, "Şifre en az bir özel karakter içermelidir");
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
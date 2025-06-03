package com.software.software_program.core.utils;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static void validateEmailFormat(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Email has invalid format: " + email);
        }
    }
}

package com.software.software_program.core.utility;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ValidationUtils {
    public static void validateEmailFormat(String email) {
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            throw new IllegalArgumentException("Email has invalid format: " + email);
        }
    }
}

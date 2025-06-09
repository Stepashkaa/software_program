package com.software.software_program.core.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.software.software-program")
public class AppConfigurationProperties {

    private Jwt jwt = new Jwt();
    private Admin admin = new Admin();

    // Конструктор для логирования значений
    public AppConfigurationProperties() {
        System.out.println("Admin email: " + admin.getEmail());
        System.out.println("JWT secret key: " + jwt.getSecretKey());
    }
    @Data
    public static class Jwt {
        private String secretKey;
        public String getSecretKey() {
            if (secretKey == null || secretKey.isEmpty()) {
                System.out.println("JWT secret key is not set!");
            }
            return secretKey;
        }
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
        private String number;
        // Геттер с логированием
        public String getEmail() {
            if (email == null || email.isEmpty()) {
                System.out.println("Admin email is not set!");
            }
            return email;
        }
    }
}

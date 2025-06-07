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

    @Data
    public static class Jwt {
        private String secretKey;
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
        private String number;
    }
}

package com.software.software_program.web.dto.authentication;

import com.software.software_program.model.enums.UserRole;
import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String email;
    private UserRole role;
}
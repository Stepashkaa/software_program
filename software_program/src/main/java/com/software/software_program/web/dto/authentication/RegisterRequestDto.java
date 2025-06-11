package com.software.software_program.web.dto.authentication;

import com.software.software_program.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
    private UserRole role;
}
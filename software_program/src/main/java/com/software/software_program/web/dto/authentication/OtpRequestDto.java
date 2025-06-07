package com.software.software_program.web.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpRequestDto {
    @NotBlank
    @Email
    private String email;
}

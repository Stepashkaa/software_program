package com.software.software_program.web.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailRequestDto {
    @NotBlank
    @Email
    private String from;

    @Email
    private String to;

    private String subject;

    private String message;
}

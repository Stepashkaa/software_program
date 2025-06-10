package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Pattern(
            regexp = "^[a-zA-Zа-яА-Я\\s]{1,50}$",
            message = "Full name must be 1-50 characters long and contain only letters and spaces"
    )
    private String fullName;

    @NotBlank
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank
    @Pattern(
            regexp = Constants.PHONE_PATTERN,
            message = "Invalid phone number format"
    )
    private String phoneNumber;

    @NotBlank
    @Pattern(
            regexp = Constants.PASSWORD_PATTERN,
            message = "Password must be 8-60 characters, include at least one uppercase letter, one lowercase letter, one digit, and one special character (!@#$%^&*()-_+=;:,./?\\|`~[]{})."
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotNull
    private UserRole role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean isEmailVerified;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> notificationIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> notificationMessages;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> softwareRequestIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> softwareRequestDescriptions;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> departmentIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> departmentNames;
}
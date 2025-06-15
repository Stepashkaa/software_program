package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.core.validation.IsoDate;
import com.software.software_program.model.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SoftwareRequestDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private String  requestDate;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private RequestStatus status;

    @NotBlank
    @Size(max = 500)
    private String description;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userName;

    @NotNull
    private Long equipmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String equipmentName;

    private Long softwareId;

    private String softwareName;

    private String requestedSoftwareName;
}
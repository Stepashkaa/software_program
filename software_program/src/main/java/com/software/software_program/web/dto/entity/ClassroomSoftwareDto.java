package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.core.validation.IsoDate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ClassroomSoftwareDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long classroomId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String classroomName;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long softwareId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String softwareName;

    @NotNull
    @IsoDate
    private LocalDate installationDate;
}
package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SoftwareDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 20)
    private String version;

    @NotBlank
    @Size(min = 1, max = 100)
    private String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> classroomSoftwareIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> classroomSoftwareNames;
}
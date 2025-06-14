package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EquipmentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotBlank
    @Size(min = 1, max = 50)
    private String type;

    @NotBlank
    @Size(min = 1, max = 50)
    private String serialNumber;

    private Long classroomId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String classroomName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> softwareIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> softwareNames;
}
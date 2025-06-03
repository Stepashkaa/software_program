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
public class DepartmentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long facultyId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String facultyName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> classroomIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> classroomNames;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> reportIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> reportNames;
}
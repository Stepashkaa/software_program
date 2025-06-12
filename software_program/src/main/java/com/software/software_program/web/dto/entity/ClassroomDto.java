package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClassroomDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

    @NotNull
    @Min(1)
    private Integer capacity;

    @NotNull
    private Long departmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> equipmentIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> equipmentNames;
}
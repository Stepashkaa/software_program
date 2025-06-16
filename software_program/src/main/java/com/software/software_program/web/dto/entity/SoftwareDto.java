package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.model.enums.TypeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
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
    private List<Long> equipmentIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> equipmentNames;

    private TypeStatus type;

    public SoftwareDto(String name, String version, String description, TypeStatus type) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.type = type;
    }
}
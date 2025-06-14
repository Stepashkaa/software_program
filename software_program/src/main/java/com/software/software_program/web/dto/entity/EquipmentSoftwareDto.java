package com.software.software_program.web.dto.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.core.validation.IsoDate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EquipmentSoftwareDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Long equipmentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String equipmentName;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private List<Long> softwareIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> softwareNames;

    @NotNull
    @IsoDate
    private String installationDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String serialNumber;
}
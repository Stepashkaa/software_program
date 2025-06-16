package com.software.software_program.web.dto.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SoftwareRequestItemDto {

    @NotBlank
    @Size(max = 50)
    private String equipmentName;

    @Size(max = 50)
    private String serialNumber;

    @NotBlank
    @Size(max = 100)
    private String softwareName;

    @Size(max = 50)
    private String softwareType;

    @Size(max = 500)
    private String softwareDescription;
}

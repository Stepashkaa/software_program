package com.software.software_program.web.dto.entity;

import com.software.software_program.core.validation.IsoDate;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EquipmentSoftwareBulkDto {

    @NotNull
    private Long equipmentId;

    @NotNull
    private List<Long> softwareIds;

    @NotNull
    @IsoDate
    private String installationDate;
}

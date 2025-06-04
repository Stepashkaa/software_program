package com.software.software_program.web.dto.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.software.software_program.core.validation.IsoDate;
import com.software.software_program.model.enums.RequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ReportDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @IsoDate
    private String generatedDate;

    @NotNull
    @IsoDate
    private String periodStart;

    @NotNull
    @IsoDate
    private String periodEnd;

    @NotBlank
    private String description;

    @NotNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> departmentIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String departmentName;

    private List<Long> requestIds;
    private List<Long> classroomIds;
    private RequestStatus status;
}
package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoftwareUsageItem {
    private String softwareName;
    private String version;
    private String classroomName;
    private LocalDate installationDate;
}
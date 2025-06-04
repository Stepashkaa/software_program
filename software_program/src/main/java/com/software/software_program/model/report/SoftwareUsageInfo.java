package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SoftwareUsageInfo {
    private String departmentName;
    private String facultyName;
    private List<SoftwareUsageItem> softwareList;
    private LocalDate reportPeriodStart;
    private LocalDate reportPeriodEnd;
}
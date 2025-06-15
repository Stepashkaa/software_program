package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SoftwareUsageInfo {
    private Long departmentId;
    private String departmentName;
    private String facultyName;
    private List<String> classroomNames;
    private List<SoftwareUsageItem> softwareList;
    private Date reportPeriodStart;
    private Date reportPeriodEnd;
}
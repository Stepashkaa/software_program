package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SoftwareUsageItem {
    private String softwareName;
    private String version;
    private String classroomName;
    private Date installationDate;
}
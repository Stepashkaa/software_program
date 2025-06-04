package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class SoftwareRequestInfo {
    private String requestNumber;
    private String requestDate;
    private String status;
    private String description;
    private String softwareName;
    private String softwareVersion;
    private String softwareDescription;
    private String classroomName;
    private String departmentName;
    private String facultyName;
    private String requesterName;
}

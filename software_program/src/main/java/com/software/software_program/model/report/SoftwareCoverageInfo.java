package com.software.software_program.model.report;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SoftwareCoverageInfo {
    private String classroomName;
    private Integer classroomCapacity;
    private String departmentName;
    private String facultyName;
    private List<SoftwareItem> softwareList;
    private boolean isFullyCovered;
}

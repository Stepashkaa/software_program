package com.software.software_program.model.report;

import com.software.software_program.web.dto.entity.SoftwareDto;
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
    private List<SoftwareDto> softwareList;
    private boolean isFullyCovered;
}

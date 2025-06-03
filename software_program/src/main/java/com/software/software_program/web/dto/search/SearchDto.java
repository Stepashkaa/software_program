package com.software.software_program.web.dto.search;

import com.software.software_program.web.dto.entity.ClassroomDto;
import com.software.software_program.web.dto.entity.DepartmentDto;
import com.software.software_program.web.dto.entity.SoftwareDto;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import com.software.software_program.web.dto.pagination.PageDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchDto {

    private PageDto<ClassroomDto> classrooms;

    private PageDto<DepartmentDto> departments;

    private PageDto<SoftwareDto> software;

    private PageDto<SoftwareRequestDto> softwareRequests;

    private List<SoftwareDto> softwareUsedByDepartment;

    private List<ClassroomDto> classroomSoftwareReport;

    private List<String> notifications;
}
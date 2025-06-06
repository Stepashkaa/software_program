package com.software.software_program.web.dto.search;

import com.software.software_program.web.dto.entity.*;
import com.software.software_program.web.dto.pagination.PageDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class SearchDto {

    private Page<ClassroomDto> classrooms;

    private Page<DepartmentDto> departments;

    private Page<SoftwareDto> software;

    private Page<FacultyDto> faculties;

    private Page<SoftwareRequestDto> softwareRequests;

    private List<SoftwareDto> softwareUsedByDepartment;

    private List<ClassroomDto> classroomSoftwareReport;

    private List<String> notifications;
}
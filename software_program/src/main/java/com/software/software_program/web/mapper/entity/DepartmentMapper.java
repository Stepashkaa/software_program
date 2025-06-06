package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
//import com.software.software_program.model.entity.ReportEntity;
import com.software.software_program.service.entity.FacultyService;
import com.software.software_program.web.dto.entity.DepartmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    private final FacultyService facultyService;

    public DepartmentDto toDto(DepartmentEntity entity) {
        DepartmentDto dto = new DepartmentDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getFaculty() != null) {
            dto.setFacultyId(entity.getFaculty().getId());
            dto.setFacultyName(entity.getFaculty().getName());
        }

        dto.setClassroomIds(
                entity.getClassrooms()
                        .stream()
                        .map(ClassroomEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setClassroomNames(
                entity.getClassrooms()
                        .stream()
                        .map(ClassroomEntity::getName)
                        .collect(Collectors.toList())
        );

//        dto.setReportIds(
//                entity.getReports()
//                        .stream()
//                        .map(ReportEntity::getId)
//                        .collect(Collectors.toList())
//        );
//        dto.setReportNames(
//                entity.getReports()
//                        .stream()
//                        .map(ReportEntity::getDescription)
//                        .collect(Collectors.toList())
//        );

        return dto;
    }

    public DepartmentEntity toEntity(DepartmentDto dto) {
        DepartmentEntity entity = new DepartmentEntity();

        // Базовые поля
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        // Факультет
        if (dto.getFacultyId() != null) {
            FacultyEntity faculty = facultyService.get(dto.getFacultyId());
            entity.setFaculty(faculty);
        }

        // Инициализация коллекций
        entity.setClassrooms(new HashSet<>());
//        entity.setReports(new HashSet<>());

        return entity;
    }
}
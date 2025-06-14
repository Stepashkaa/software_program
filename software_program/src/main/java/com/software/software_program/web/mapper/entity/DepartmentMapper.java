package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
//import com.software.software_program.model.entity.ReportEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.FacultyService;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.ClassroomDto;
import com.software.software_program.web.dto.entity.DepartmentDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    private final FacultyService facultyService;
    private final UserService userService;

    public DepartmentDto toDto(DepartmentEntity entity) {
        DepartmentDto dto = new DepartmentDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        if (entity.getFaculty() != null) {
            Hibernate.initialize(entity.getFaculty());
            dto.setFacultyId(entity.getFaculty().getId());
            dto.setFacultyName(entity.getFaculty().getName());
        } else {
            dto.setFacultyName("Нет факультета");
        }

        if (entity.getHead() != null) {
            dto.setHeadId(entity.getHead().getId());
            dto.setHeadName(entity.getHead().getFullName());
        } else {
            dto.setHeadName("Нет заведующего");
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
        // Заведующий
        if (dto.getHeadId() != null) {
            UserEntity head = userService.get(dto.getHeadId());
            entity.setHead(head);
        } else {
            throw new IllegalArgumentException("Head ID must not be null");
        }

        // Инициализация коллекций
        entity.setClassrooms(new HashSet<>());

        return entity;
    }
}
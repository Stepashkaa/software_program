package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.service.entity.DepartmentService;
import com.software.software_program.web.dto.entity.FacultyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FacultyMapper {

    private final DepartmentService departmentService;

    public FacultyDto toDto(FacultyEntity entity) {
        FacultyDto dto = new FacultyDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        dto.setDepartmentIds(
                entity.getDepartments()
                        .stream()
                        .map(DepartmentEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setDepartmentNames(
                entity.getDepartments()
                        .stream()
                        .map(DepartmentEntity::getName)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public FacultyEntity toEntity(FacultyDto dto) {
        FacultyEntity entity = new FacultyEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());

        if (dto.getDepartmentIds() != null && !dto.getDepartmentIds().isEmpty()) {
            Set<DepartmentEntity> departments = dto.getDepartmentIds().stream()
                    .map(departmentService::get) // Получаем кафедры по ID
                    .collect(Collectors.toSet());
            entity.setDepartments(departments);
        } else {
            entity.setDepartments(new HashSet<>()); // Если кафедры не указаны, оставляем пустой набор
        }

        return entity;
    }
}
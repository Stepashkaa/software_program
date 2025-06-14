package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.service.entity.DepartmentService;
import com.software.software_program.web.dto.entity.ClassroomDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClassroomMapper {

    private final ModelMapper modelMapper;
    private final DepartmentService departmentService;

    public ClassroomDto toDto(ClassroomEntity entity) {
        ClassroomDto dto = modelMapper.map(entity, ClassroomDto.class);

        if (entity.getDepartment() != null) {
            dto.setDepartmentId(entity.getDepartment().getId());
            dto.setDepartmentName(entity.getDepartment().getName());
        } else {
            dto.setDepartmentName("Нет кафедры");
        }

        dto.setEquipmentIds(
                entity.getEquipments()
                        .stream()
                        .map(EquipmentEntity::getId)
                        .collect(Collectors.toList())
        );

        dto.setEquipmentNames(
                entity.getEquipments()
                        .stream()
                        .map(EquipmentEntity::getName) // <-- добавлено
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public ClassroomEntity toEntity(ClassroomDto dto) {
        ClassroomEntity entity = modelMapper.map(dto, ClassroomEntity.class);

        if (dto.getDepartmentId() != null) {
            DepartmentEntity department = departmentService.get(dto.getDepartmentId());
            entity.setDepartment(department);
        }

        if (entity.getEquipments() == null) {
            entity.setEquipments(new HashSet<>());
        }

        return entity;
    }
}

package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.service.entity.ClassroomService;
import com.software.software_program.web.dto.entity.EquipmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EquipmentMapper {

    private final ClassroomService classroomService;

    public EquipmentDto toDto(EquipmentEntity entity) {
        EquipmentDto dto = new EquipmentDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setSerialNumber(entity.getSerialNumber());

        if (entity.getClassroom() != null) {
            dto.setClassroomId(entity.getClassroom().getId());
            dto.setClassroomName(entity.getClassroom().getName());
        } else {
            dto.setClassroomName("Нет аудитории");
        }

        // Собираем все уникальные SoftwareEntity из всех EquipmentSoftwareEntity
        var softwares = entity.getEquipmentSoftwares().stream()
                .flatMap(es -> es.getSoftwares().stream())
                .collect(Collectors.toSet());

        dto.setSoftwareIds(
                softwares.stream()
                        .map(SoftwareEntity::getId)
                        .collect(Collectors.toList())
        );

        dto.setSoftwareNames(
                softwares.stream()
                        .map(SoftwareEntity::getName)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public EquipmentEntity toEntity(EquipmentDto dto) {
        EquipmentEntity entity = new EquipmentEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setSerialNumber(dto.getSerialNumber());

        if (dto.getClassroomId() != null) {
            ClassroomEntity classroom = classroomService.get(dto.getClassroomId());
            entity.setClassroom(classroom);
        }

        // Привязка ПО будет происходить отдельно через EquipmentSoftwareService
        return entity;
    }
}

package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentEntity;
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
        }

        dto.setSoftwareIds(
                entity.getEquipmentSoftwares().stream()
                        .map(es -> es.getSoftware().getId())
                        .collect(Collectors.toList())
        );

        dto.setSoftwareNames(
                entity.getEquipmentSoftwares().stream()
                        .map(es -> es.getSoftware().getName())
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

        return entity;
    }
}

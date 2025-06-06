package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.web.dto.entity.SoftwareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SoftwareMapper {

    public SoftwareDto toDto(SoftwareEntity entity) {
        SoftwareDto dto = new SoftwareDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setVersion(entity.getVersion());
        dto.setDescription(entity.getDescription());

        dto.setClassroomSoftwareIds(
                entity.getClassroomSoftwares()
                        .stream()
                        .map(cs -> cs.getId())
                        .collect(Collectors.toList())
        );
        dto.setClassroomSoftwareNames(
                entity.getClassroomSoftwares()
                        .stream()
                        .map(cs -> cs.getClassroom().getName()) // Предполагается, что у ClassroomSoftware есть связь с Classroom
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public SoftwareEntity toEntity(SoftwareDto dto) {
        SoftwareEntity entity = new SoftwareEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setVersion(dto.getVersion());
        entity.setDescription(dto.getDescription());

        entity.setClassroomSoftwares(new HashSet<>());

        return entity;
    }
}
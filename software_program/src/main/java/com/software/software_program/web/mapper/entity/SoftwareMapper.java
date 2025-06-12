package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.*;
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

        dto.setEquipmentIds(
                entity.getEquipmentSoftwares().stream()
                        .map(es -> es.getEquipment().getId())
                        .collect(Collectors.toList())
        );
        dto.setEquipmentNames(
                entity.getEquipmentSoftwares().stream()
                        .map(es -> es.getEquipment().getName())
                        .collect(Collectors.toList())
        );

        dto.setSoftwareRequestIds(
                entity.getSoftwareRequests().stream()
                        .map(SoftwareRequestEntity::getId)
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

        entity.setEquipmentSoftwares(new HashSet<>());

        return entity;
    }
}
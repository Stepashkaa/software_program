package com.software.software_program.web.mapper.entity;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.service.entity.ClassroomService;
import com.software.software_program.service.entity.EquipmentService;
import com.software.software_program.service.entity.SoftwareService;
import com.software.software_program.web.dto.entity.EquipmentSoftwareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EquipmentSoftwareMapper {

    private final EquipmentService equipmentService;
    private final SoftwareService softwareService;

    public EquipmentSoftwareDto toDto(EquipmentSoftwareEntity entity) {
        EquipmentSoftwareDto dto = new EquipmentSoftwareDto();

        dto.setId(entity.getId());

        // Защита от LazyInitializationException
        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
            try {
                dto.setEquipmentName(entity.getEquipment().getName());
                dto.setSerialNumber(entity.getEquipment().getSerialNumber());
            } catch (Exception e) {
                dto.setEquipmentName(null);
                dto.setSerialNumber(null);
            }
        }

        dto.setInstallationDate(Formatter.format(entity.getInstallationDate()));

        try {
            dto.setSoftwareIds(entity.getSoftwares().stream()
                    .map(SoftwareEntity::getId)
                    .toList());

            dto.setSoftwareNames(entity.getSoftwares().stream()
                    .map(SoftwareEntity::getName)
                    .toList());
        } catch (Exception e) {
            dto.setSoftwareIds(null);
            dto.setSoftwareNames(null);
        }

        return dto;
    }

    public EquipmentSoftwareEntity toEntity(EquipmentSoftwareDto dto) {
        EquipmentSoftwareEntity entity = new EquipmentSoftwareEntity();

        entity.setId(dto.getId());
        entity.setEquipment(equipmentService.get(dto.getEquipmentId()));
        entity.setInstallationDate(Formatter.parse(dto.getInstallationDate()));

        Set<SoftwareEntity> softwareSet = dto.getSoftwareIds().stream()
                .map(softwareService::get)
                .collect(Collectors.toSet());

        entity.setSoftwares(softwareSet);

        return entity;
    }
}

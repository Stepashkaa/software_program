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

@Component
@RequiredArgsConstructor
public class EquipmentSoftwareMapper {

    private final EquipmentService equipmentService;
    private final SoftwareService softwareService;

    public EquipmentSoftwareDto toDto(EquipmentSoftwareEntity entity) {
        EquipmentSoftwareDto dto = new EquipmentSoftwareDto();

        dto.setId(entity.getId());
        dto.setInstallationDate(Formatter.format(entity.getInstallationDate()));

        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
            dto.setEquipmentName(entity.getEquipment().getName());
        }
        if (entity.getSoftware() != null) {
            dto.setSoftwareId(entity.getSoftware().getId());
            dto.setSoftwareName(entity.getSoftware().getName());
        }

        return dto;
    }

    public EquipmentSoftwareEntity toEntity(EquipmentSoftwareDto dto) {
        EquipmentSoftwareEntity entity = new EquipmentSoftwareEntity();

        entity.setId(dto.getId());

        if (dto.getEquipmentId() != null) {
            entity.setEquipment(equipmentService.get(dto.getEquipmentId()));
        }

        if (dto.getSoftwareId() != null) {
            SoftwareEntity software = softwareService.get(dto.getSoftwareId());
            entity.setSoftware(software);
        }

        entity.setInstallationDate(Formatter.parse(dto.getInstallationDate()));

        return entity;
    }
}
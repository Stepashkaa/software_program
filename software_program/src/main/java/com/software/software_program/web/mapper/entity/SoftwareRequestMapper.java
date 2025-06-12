package com.software.software_program.web.mapper.entity;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.EquipmentService;
import com.software.software_program.service.entity.EquipmentSoftwareService;
import com.software.software_program.service.entity.SoftwareService;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoftwareRequestMapper {

    private final UserService userService;
    private final EquipmentService equipmentService;
    private final SoftwareService softwareService;
    private final EquipmentSoftwareService classroomSoftwareService;

    public SoftwareRequestDto toDto(SoftwareRequestEntity entity) {
        SoftwareRequestDto dto = new SoftwareRequestDto();

        dto.setId(entity.getId());
        dto.setRequestDate(Formatter.format(entity.getRequestDate()));
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getFullName());
        }

        if (entity.getEquipment() != null) {
            dto.setEquipmentId(entity.getEquipment().getId());
            dto.setEquipmentName(entity.getEquipment().getName());
        }
        if (entity.getSoftware() != null) {
            dto.setSoftwareId(entity.getSoftware().getId());
            dto.setSoftwareName(entity.getSoftware().getName());
        }
        dto.setRequestedSoftwareName(entity.getRequestedSoftwareName());

        return dto;
    }

    public SoftwareRequestEntity toEntity(SoftwareRequestDto dto) {
        SoftwareRequestEntity entity = new SoftwareRequestEntity();

        entity.setId(dto.getId());
        entity.setRequestDate(Formatter.parse(dto.getRequestDate()));
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());

        if (dto.getUserId() != null) {
            UserEntity user = userService.get(dto.getUserId());
            entity.setUser(user);
        }

        if (dto.getEquipmentId() != null) {
            entity.setEquipment(equipmentService.get(dto.getEquipmentId()));
        }
        if (dto.getSoftwareId() != null) {
            entity.setSoftware(softwareService.get(dto.getSoftwareId()));
        }
        entity.setRequestedSoftwareName(dto.getRequestedSoftwareName());

        return entity;
    }
}

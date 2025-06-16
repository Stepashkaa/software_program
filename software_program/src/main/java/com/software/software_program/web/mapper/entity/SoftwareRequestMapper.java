package com.software.software_program.web.mapper.entity;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.*;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.SoftwareRequestItemEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.EquipmentService;
import com.software.software_program.service.entity.EquipmentSoftwareService;
import com.software.software_program.service.entity.SoftwareService;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import com.software.software_program.web.dto.entity.SoftwareRequestItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoftwareRequestMapper {

    private final UserService userService;

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

        dto.setRequestItems(
                entity.getRequestItems().stream()
                        .map(this::toItemDto)
                        .toList()
        );

        return dto;
    }

    private SoftwareRequestItemDto toItemDto(SoftwareRequestItemEntity item) {
        var dto = new SoftwareRequestItemDto();
        dto.setEquipmentName(item.getEquipmentName());
        dto.setSerialNumber(item.getSerialNumber());
        dto.setSoftwareName(item.getSoftwareName());
        dto.setSoftwareType(item.getSoftwareType());
        dto.setSoftwareDescription(item.getSoftwareDescription());
        return dto;
    }

    public SoftwareRequestEntity toEntity(SoftwareRequestDto dto) {
        var entity = new SoftwareRequestEntity();
        entity.setId(dto.getId());
        entity.setRequestDate(Formatter.parse(dto.getRequestDate()));
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());

        if (dto.getUserId() != null) {
            entity.setUser(userService.get(dto.getUserId()));
        }

        var items = dto.getRequestItems().stream()
                .map(itemDto -> toItemEntity(itemDto, entity))
                .toList();

        entity.setRequestItems(items);
        return entity;
    }

    private SoftwareRequestItemEntity toItemEntity(SoftwareRequestItemDto dto, SoftwareRequestEntity request) {
        var item = new SoftwareRequestItemEntity();
        item.setSoftwareRequest(request);
        item.setEquipmentName(dto.getEquipmentName());
        item.setSerialNumber(dto.getSerialNumber());
        item.setSoftwareName(dto.getSoftwareName());
        item.setSoftwareType(dto.getSoftwareType());
        item.setSoftwareDescription(dto.getSoftwareDescription());
        return item;
    }
}


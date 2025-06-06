package com.software.software_program.web.mapper.entity;

import com.software.software_program.core.utils.Formatter;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.ClassroomSoftwareService;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.SoftwareRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoftwareRequestMapper {

    private final UserService userService;
    private final ClassroomSoftwareService classroomSoftwareService;

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

        if (entity.getClassroomSoftware() != null) {
            dto.setClassroomSoftwareId(entity.getClassroomSoftware().getId());
            dto.setSoftwareName(entity.getClassroomSoftware().getSoftware().getName());
        }

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

        if (dto.getClassroomSoftwareId() != null) {
            ClassroomSoftwareEntity classroomSoftware = classroomSoftwareService.get(dto.getClassroomSoftwareId());
            entity.setClassroomSoftware(classroomSoftware);
        }

        return entity;
    }
}

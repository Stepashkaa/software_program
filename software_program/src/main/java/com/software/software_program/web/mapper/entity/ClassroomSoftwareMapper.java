package com.software.software_program.web.mapper.entity;

import com.software.software_program.core.utility.Formatter;
import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.service.entity.ClassroomService;
import com.software.software_program.service.entity.SoftwareService;
import com.software.software_program.web.dto.entity.ClassroomSoftwareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClassroomSoftwareMapper {

    private final ClassroomService classroomService;
    private final SoftwareService softwareService;

    public ClassroomSoftwareDto toDto(ClassroomSoftwareEntity entity) {
        ClassroomSoftwareDto dto = new ClassroomSoftwareDto();

        dto.setId(entity.getId());
        dto.setInstallationDate(Formatter.format(entity.getInstallationDate()));

        if (entity.getClassroom() != null) {
            dto.setClassroomId(entity.getClassroom().getId());
            dto.setClassroomName(entity.getClassroom().getName());
        }
        if (entity.getSoftware() != null) {
            dto.setSoftwareId(entity.getSoftware().getId());
            dto.setSoftwareName(entity.getSoftware().getName());
        }

        return dto;
    }

    public ClassroomSoftwareEntity toEntity(ClassroomSoftwareDto dto) {
        ClassroomSoftwareEntity entity = new ClassroomSoftwareEntity();

        entity.setId(dto.getId());

        if (dto.getClassroomId() != null) {
            ClassroomEntity classroom = classroomService.get(dto.getClassroomId());
            entity.setClassroom(classroom);
        }

        if (dto.getSoftwareId() != null) {
            SoftwareEntity software = softwareService.get(dto.getSoftwareId());
            entity.setSoftware(software);
        }

        entity.setInstallationDate(Formatter.parse(dto.getInstallationDate()));

        return entity;
    }
}
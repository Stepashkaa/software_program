package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.DepartmentEntity;
import com.software.software_program.model.entity.NotificationEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.web.dto.entity.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public UserDto toDto(UserEntity entity) {
        UserDto dto = new UserDto();

        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setRole(entity.getRole());

        dto.setNotificationIds(
                entity.getNotifications()
                        .stream()
                        .map(NotificationEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setNotificationMessages(
                entity.getNotifications()
                        .stream()
                        .map(NotificationEntity::getMessage)
                        .collect(Collectors.toList())
        );

        dto.setSoftwareRequestIds(
                entity.getSoftwareRequests()
                        .stream()
                        .map(SoftwareRequestEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setSoftwareRequestDescriptions(
                entity.getSoftwareRequests()
                        .stream()
                        .map(SoftwareRequestEntity::getDescription)
                        .collect(Collectors.toList())
        );
        dto.setDepartmentIds(
                entity.getDepartments()
                        .stream()
                        .map(DepartmentEntity::getId)
                        .collect(Collectors.toList())
        );
        dto.setDepartmentNames(
                entity.getDepartments()
                        .stream()
                        .map(DepartmentEntity::getName)
                        .collect(Collectors.toList())
        );

        dto.setEmailNotificationEnabled(entity.isEmailNotificationEnabled());
        dto.setWebNotificationEnabled(entity.isWebNotificationEnabled());

        return dto;
    }

    public UserEntity toEntity(UserDto dto) {
        UserEntity entity = new UserEntity();

        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setPassword(dto.getPassword());
        entity.setRole(dto.getRole());

        entity.setNotifications(new HashSet<>());
        entity.setSoftwareRequests(new HashSet<>());
        entity.setDepartments(new HashSet<>());
        entity.setEmailNotificationEnabled(dto.isEmailNotificationEnabled());
        entity.setWebNotificationEnabled(dto.isWebNotificationEnabled());


        return entity;
    }
}

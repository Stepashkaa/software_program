package com.software.software_program.web.mapper.entity;

import com.software.software_program.model.entity.NotificationEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final UserService userService;

    public NotificationDto toDto(NotificationEntity entity) {
        NotificationDto dto = new NotificationDto();

        dto.setId(entity.getId());
        dto.setMessage(entity.getMessage());
        dto.setSentDate(entity.getSentDate());
        dto.setRead(entity.isRead());

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getFullName());
        }

        return dto;
    }

    public NotificationEntity toEntity(NotificationDto dto) {
        NotificationEntity entity = new NotificationEntity();

        entity.setId(dto.getId());
        entity.setMessage(dto.getMessage());
        entity.setSentDate(dto.getSentDate());
        entity.setRead(dto.isRead());

        if (dto.getUserId() != null) {
            UserEntity user = userService.get(dto.getUserId());
            entity.setUser(user);
        }

        return entity;
    }
}
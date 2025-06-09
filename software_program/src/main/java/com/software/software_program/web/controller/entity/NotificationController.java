package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.web.dto.entity.NotificationDto;
import com.software.software_program.service.entity.NotificationService;
import com.software.software_program.web.mapper.entity.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    @GetMapping("/user/{userId}")
    public List<NotificationDto> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(name = "isRead", required = false) Boolean isRead
    ) {
        return notificationService.getUserNotifications(userId, isRead).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDto sendNotification(
            @RequestParam String message,
            @RequestParam Long userId
    ) {
        return notificationMapper.toDto(
                notificationService.sendNotification(message, null) // Предполагается, что userId используется внутри сервиса
        );
    }

    @PutMapping("/{id}/mark-as-read")
    public NotificationDto markAsRead(@PathVariable Long id) {
        return notificationMapper.toDto(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }
}
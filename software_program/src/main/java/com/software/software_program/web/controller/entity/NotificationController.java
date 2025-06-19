package com.software.software_program.web.controller.entity;

import com.software.software_program.core.configuration.Constants;
import com.software.software_program.model.entity.NotificationEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.service.entity.UserService;
import com.software.software_program.web.dto.entity.NotificationDto;
import com.software.software_program.service.entity.NotificationService;
import com.software.software_program.web.mapper.entity.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.API_URL + Constants.ADMIN_PREFIX + "/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
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

    @GetMapping("/{id}/notifications/setting")
    public Map<String, Boolean> getWebNotificationSetting(@PathVariable("id") Long id) {
        UserEntity u = userService.get(id);  // <-- вместо getById
        return Map.of("webNotificationEnabled", u.isWebNotificationEnabled());
    }

    @PutMapping("/{id}/notifications/setting")
    public Map<String, Boolean> updateWebNotificationSetting(
            @PathVariable("id") Long id,
            @RequestBody Map<String, Boolean> body
    ) {
        boolean enabled = Boolean.TRUE.equals(body.get("webNotificationEnabled"));
        UserEntity u = userService.get(id);
        u.setWebNotificationEnabled(enabled);
        UserEntity updated = userService.update(id, u);
        return Map.of("webNotificationEnabled", updated.isWebNotificationEnabled());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDto sendNotification(
            @RequestParam String message,
            @RequestParam Long userId
    ) {
        UserEntity user = userService.get(userId);
        NotificationEntity sent = notificationService.sendNotification(message, user);
        if (sent == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "У пользователя отключены веб-уведомления"
            );
        }
        return notificationMapper.toDto(sent);
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
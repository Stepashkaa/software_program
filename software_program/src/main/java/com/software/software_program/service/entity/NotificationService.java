package com.software.software_program.service.entity;

import com.software.software_program.model.entity.NotificationEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.model.enums.UserRole;
import com.software.software_program.repository.NotificationRepository;
import com.software.software_program.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final  UserRepository userRepository;


    private NotificationEntity get(long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    }

    @Transactional
    public List<NotificationEntity> sendNotificationToAdmins(String message) {
        validate(message);
        List<UserEntity> admins = userRepository.findByRole(UserRole.ADMIN);
        return admins.stream()
                .map(user -> new NotificationEntity(message, LocalDateTime.now(), user, false))
                .map(notificationRepository::save)
                .toList();
    }

    @Transactional
    public NotificationEntity sendNotification(String message, UserEntity user) {
        validate(message, user);
        if (!user.isWebNotificationEnabled()) {
            return null;
        }
        NotificationEntity entity = new NotificationEntity(message, LocalDateTime.now(), user, false);
        return notificationRepository.save(entity);
    }


    @Transactional(readOnly = true)
    public List<NotificationEntity> getUserNotifications(Long userId, Boolean isRead) {
        if (isRead != null) {
            return notificationRepository.findByUserIdAndIsRead(userId, isRead);
        }
        return notificationRepository.findByUserId(userId);
    }

    @Transactional
    public NotificationEntity markAsRead(long id) {
        NotificationEntity existsEntity = get(id);
        existsEntity.setRead(true);
        return notificationRepository.save(existsEntity);
    }

    @Transactional
    public void deleteNotification(long id) {
        NotificationEntity existsEntity = get(id);
        notificationRepository.delete(existsEntity);
    }

    private void validate(String message, UserEntity user) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must not be null or empty");
        }
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
    }
    private void validate(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must not be null or empty");
        }
    }


}
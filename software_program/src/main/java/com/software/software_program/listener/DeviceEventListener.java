package com.software.software_program.listener;

import com.software.software_program.model.entity.*;

import com.software.software_program.service.entity.NotificationService;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DeviceEventListener implements PostDeleteEventListener, PostUpdateEventListener, PostInsertEventListener {

    private final NotificationService notificationService;

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        if (event.getEntity() instanceof ClassroomSoftwareEntity classroomSoftware) {
            // Уведомление о удалении ПО из аудитории
            sendNotification("Программное обеспечение удалено из аудитории", classroomSoftware);
        } else if (event.getEntity() instanceof EquipmentEntity equipment) {
            // Уведомление о удалении оборудования
            sendNotification("Оборудование удалено из аудитории", equipment);
        }
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (event.getEntity() instanceof SoftwareRequestEntity softwareRequest) {
            // Уведомление об изменении статуса заявки
            String message = "Статус заявки на ПО изменен на " + softwareRequest.getStatus();
            sendNotification(message, softwareRequest.getUser());
        } else if (event.getEntity() instanceof ClassroomSoftwareEntity classroomSoftware) {
            // Уведомление об обновлении ПО в аудитории
            sendNotification("Программное обеспечение обновлено в аудитории", classroomSoftware);
        } else if (event.getEntity() instanceof EquipmentEntity equipment) {
            // Уведомление об обновлении оборудования
            sendNotification("Оборудование обновлено в аудитории", equipment);
        }
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (event.getEntity() instanceof SoftwareRequestEntity softwareRequest) {
            // Уведомление о создании новой заявки на ПО
            String message = "Создана новая заявка на ПО для аудитории";
            sendNotification(message, softwareRequest.getUser());
        } else if (event.getEntity() instanceof ClassroomSoftwareEntity classroomSoftware) {
            // Уведомление о добавлении ПО в аудиторию
            sendNotification("Программное обеспечение добавлено в аудиторию", classroomSoftware);
        } else if (event.getEntity() instanceof EquipmentEntity equipment) {
            // Уведомление о добавлении оборудования в аудиторию
            sendNotification("Оборудование добавлено в аудиторию", equipment);
        }
    }

    private void sendNotification(String message, BaseEntity entity) {
        if (entity instanceof ClassroomSoftwareEntity classroomSoftware) {
            notificationService.sendNotification(
                    message + ": " + classroomSoftware.getSoftware().getName(),
                    classroomSoftware.getClassroom().getDepartment().getUser()
            );
        } else if (entity instanceof EquipmentEntity equipment) {
            notificationService.sendNotification(
                    message + ": " + equipment.getName(),
                    equipment.getClassroom().getDepartment().getUser()
            );
        } else if (entity instanceof UserEntity user) {
            notificationService.sendNotification(message, user);
        }
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }
}
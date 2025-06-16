package com.software.software_program.service.entity;

import com.software.software_program.model.entity.*;
import com.software.software_program.model.enums.RequestStatus;
import com.software.software_program.repository.SoftwareRequestRepository;
import com.software.software_program.core.error.NotFoundException;
import com.software.software_program.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoftwareRequestService extends AbstractEntityService<SoftwareRequestEntity> {

    private final SoftwareRequestRepository softwareRequestRepo;
    private final UserRepository userRequestRepo;
    private final NotificationService notificationService;
    private final EquipmentService equipmentService;

    @Transactional(readOnly = true)
    public Page<SoftwareRequestEntity> getAllByFilters(
            RequestStatus status,
            Long userId,
            Pageable pageable
    ) {
        return softwareRequestRepo.findAllByFilters(status, userId, pageable);
    }

    @Transactional(readOnly = true)
    public List<SoftwareRequestEntity> getAll(RequestStatus status, Long userId) {
        if (status != null && userId != null) {
            UserEntity user = userRequestRepo.findById(userId)
                    .orElseThrow(() -> new NotFoundException(UserEntity.class, userId));
            return softwareRequestRepo.findByStatusAndUser(status, user);
        } else if (status != null) {
            return softwareRequestRepo.findByStatus(status);
        } else if (userId != null) {
            return softwareRequestRepo.findByUserId(userId);
        }
        return softwareRequestRepo.findAll().stream().toList();
    }

    @Transactional(readOnly = true)
    public SoftwareRequestEntity get(long id) {
        return softwareRequestRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(SoftwareRequestEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<SoftwareRequestEntity> getByIds(List<Long> ids) {
        return softwareRequestRepo.findAllById(ids);
    }

    @Transactional
    public SoftwareRequestEntity create(SoftwareRequestEntity entity) {
        entity.setStatus(RequestStatus.PENDING);
        validate(entity, null);
        for (SoftwareRequestItemEntity item : entity.getRequestItems()) {
            item.setSoftwareRequest(entity);
        }
        SoftwareRequestEntity saved = softwareRequestRepo.save(entity);

        notificationService.sendNotification(
                "Ваша заявка №" + saved.getId() + " успешно создана и ожидает обработки.",
                saved.getUser()
        );

        return saved;
    }



    @Transactional
    public SoftwareRequestEntity update(long id, RequestStatus status, String description) {
        if (description != null) {
            validateStringField(description, "Description");
        }

        SoftwareRequestEntity existsEntity = get(id);

        if (status != null) {
            existsEntity.setStatus(status);

            if (existsEntity.getUser() == null) {
                throw new IllegalArgumentException("User must not be null");
            }

            String message = String.format("Статус вашей заявки изменен на '%s'", status);
            notificationService.sendNotification(message, existsEntity.getUser());
        }

        if (description != null) {
            existsEntity.setDescription(description);
        }

        SoftwareRequestEntity exists = get(id);
        if (status != null) {
            exists.setStatus(status);
            SoftwareRequestEntity updated = softwareRequestRepo.save(exists);

            // уведомляем пользователя о смене статуса
            notificationService.sendNotification(
                    "Статус вашей заявки №" + id + " изменён на «" + status + "».",
                    updated.getUser()
            );
            return updated;
        }

        validate(existsEntity, id);
        return softwareRequestRepo.save(existsEntity);
    }

    @Transactional
    public SoftwareRequestEntity delete(long id) {
        SoftwareRequestEntity existsEntity = get(id);
        softwareRequestRepo.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(SoftwareRequestEntity entity, Long id) {
        if (entity == null) throw new IllegalArgumentException("Entity is null");

        if (entity.getRequestDate() == null || entity.getRequestDate().after(new Date())) {
            throw new IllegalArgumentException("Invalid request date");
        }

        validateStringField(entity.getDescription(), "Description");

        if (entity.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        if (entity.getRequestItems() == null || entity.getRequestItems().isEmpty()) {
            throw new IllegalArgumentException("Request must contain at least one equipment+software item");
        }

        for (SoftwareRequestItemEntity item : entity.getRequestItems()) {
            if (item.getEquipmentName() == null || item.getEquipmentName().isBlank()) {
                throw new IllegalArgumentException("Equipment name is required in each item");
            }
            if (item.getSoftwareName() == null || item.getSoftwareName().isBlank()) {
                throw new IllegalArgumentException("Software name is required in each item");
            }
        }
    }


}
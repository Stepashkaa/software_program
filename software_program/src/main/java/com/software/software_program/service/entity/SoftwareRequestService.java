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

    @Transactional(readOnly = true)
    public Page<SoftwareRequestEntity> getAllByFilters(
            RequestStatus status,
            Long userId,
            Long classroomSoftwareId,
            Pageable pageable
    ) {
        return softwareRequestRepo.findAllByFilters(status, userId, classroomSoftwareId, pageable);
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
    public SoftwareRequestEntity create(Date requestDate, String description, UserEntity user,
                                        EquipmentEntity equipment, SoftwareEntity software, String requestedSoftwareName) {
        SoftwareRequestEntity entity = new SoftwareRequestEntity(requestDate, RequestStatus.PENDING, description, user, equipment, software, requestedSoftwareName);
        validate(entity, null);
        return softwareRequestRepo.save(entity);
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
        if (entity == null) {
            throw new IllegalArgumentException("SoftwareRequest entity is null");
        }

        if (entity.getRequestDate() == null || entity.getRequestDate().after(new Date())) {
            throw new IllegalArgumentException("Request date must not be null or in the future");
        }

        validateStringField(entity.getDescription(), "Description");

        if (entity.getUser() == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        if (entity.getEquipment() == null) {
            throw new IllegalArgumentException("Equipment must not be null");
        }

        // Проверка на дубликат (user + equipment + requestDate)
        boolean exists = softwareRequestRepo.findByUserEquipmentAndDate(
                entity.getUser().getId(),
                entity.getEquipment().getId(),
                entity.getRequestDate()
        ).isPresent();

        if (exists && (id == null || !softwareRequestRepo.findByUserEquipmentAndDate(
                entity.getUser().getId(),
                entity.getEquipment().getId(),
                entity.getRequestDate()
        ).get().getId().equals(id))) {
            throw new IllegalArgumentException(
                    "Software request for this user, equipment and date already exists"
            );
        }
    }

}
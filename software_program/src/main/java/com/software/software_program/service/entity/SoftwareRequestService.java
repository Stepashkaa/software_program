package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.model.entity.UserEntity;
import com.software.software_program.model.enums.RequestStatus;
import com.software.software_program.repository.SoftwareRequestRepository;
import com.software.software_program.core.eror.NotFoundException;
import com.software.software_program.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
    public SoftwareRequestEntity create(LocalDate requestDate, String description, UserEntity user, ClassroomSoftwareEntity classroomSoftware) {
        validateFields(requestDate, description, user, classroomSoftware);
        SoftwareRequestEntity entity = new SoftwareRequestEntity(requestDate, RequestStatus.PENDING, description, user, classroomSoftware);
        return softwareRequestRepo.save(entity);
    }

    @Transactional
    public SoftwareRequestEntity update(long id, RequestStatus status, String description) {
        // Проверка описания
        if (description != null) {
            validateStringField(description, "Description");
        }
        SoftwareRequestEntity existsEntity = get(id);
        if (status != null) {
            existsEntity.setStatus(status);

            // Проверка, что пользователь существует
            if (existsEntity.getUser() == null) {
                throw new IllegalArgumentException("User must not be null");
            }
            // Отправка уведомления о изменении статуса
            String message = String.format("Статус вашей заявки изменен на '%s'", status);
            notificationService.sendNotification(message, existsEntity.getUser());
        }
        if (description != null) {
            existsEntity.setDescription(description);
        }
        return softwareRequestRepo.save(existsEntity);
    }

    @Transactional
    public SoftwareRequestEntity delete(long id) {
        SoftwareRequestEntity existsEntity = get(id);
        softwareRequestRepo.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(SoftwareRequestEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("SoftwareRequest entity is null");
        }
        validateFields(entity.getRequestDate(), entity.getDescription(), entity.getUser(), entity.getClassroomSoftware());
    }

    private void validateFields(LocalDate requestDate, String description, UserEntity user, ClassroomSoftwareEntity classroomSoftware) {
        if (requestDate == null || requestDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Request date must not be null or in the future");
        }
        validateStringField(description, "Description");
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        if (classroomSoftware == null) {
            throw new IllegalArgumentException("ClassroomSoftware must not be null");
        }
    }
}
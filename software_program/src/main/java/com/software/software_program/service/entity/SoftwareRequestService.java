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
        SoftwareRequestEntity existing = get(id);

        boolean needNotifyStatus = status != null && status != existing.getStatus();
        boolean needNotifyDescription = description != null && !description.equals(existing.getDescription());

        if (description != null) {
            validateStringField(description, "Description");
            existing.setDescription(description);
        }
        if (status != null) {
            existing.setStatus(status);
        }

        SoftwareRequestEntity updated = softwareRequestRepo.save(existing);

        if (needNotifyStatus) {
            String msg = String.format("Статус вашей заявки №%d изменён на «%s».", id, updated.getStatus());
            notificationService.sendNotification(msg, updated.getUser());
        }
        if (needNotifyDescription) {
            String msg = String.format("Описание вашей заявки №%d было обновлено.", id);
            notificationService.sendNotification(msg, updated.getUser());
        }

        return updated;
    }

    @Transactional
    public SoftwareRequestEntity delete(long id) {
        SoftwareRequestEntity existing = get(id);
        Long requestId = existing.getId();
        UserEntity user = existing.getUser();

        softwareRequestRepo.delete(existing);

        String msg = String.format("Ваша заявка №%d была удалена.", requestId);
        notificationService.sendNotification(msg, user);

        return existing;
    }


    @Override
    protected void validate(SoftwareRequestEntity entity, Long id) {
        if (entity == null) throw new IllegalArgumentException("Entity is null");

        Date now = new Date();
        if (entity.getRequestDate() == null || entity.getRequestDate().after(now)) {
            throw new IllegalArgumentException("Дата запроса не должна быть пустой или в будущем");
        }

        validateStringField(entity.getDescription(), "Description");

        if (entity.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        List<SoftwareRequestItemEntity> items = entity.getRequestItems();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Заявка должна содержать хотя бы один пункт");
        }
        Set<String> seen = new HashSet<>();
        for (SoftwareRequestItemEntity item : items) {
            if (item.getEquipmentName() == null || item.getEquipmentName().isBlank())
                throw new IllegalArgumentException("У каждого пункта должно быть имя оборудования");
            if (item.getSoftwareName() == null || item.getSoftwareName().isBlank())
                throw new IllegalArgumentException("У каждого пункта должно быть имя ПО");

            // проверка длины названий
            if (item.getEquipmentName().length() > 100 || item.getSoftwareName().length() > 100) {
                throw new IllegalArgumentException("Имя оборудования и ПО не должны превышать 100 символов");
            }

            // дублирование
            String key = item.getEquipmentName().trim().toLowerCase() + "::" +
                    item.getSoftwareName().trim().toLowerCase();
            if (!seen.add(key)) {
                throw new IllegalArgumentException(
                        "Дублирующийся пункт в заявке: " + item.getEquipmentName() + " / " + item.getSoftwareName()
                );
            }
        }
    }


}
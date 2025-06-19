package com.software.software_program.service.entity;

import com.software.software_program.core.error.FileNotFoundException;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.repository.EquipmentSoftwareRepository;
import com.software.software_program.repository.SoftwareRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SoftwareService extends AbstractEntityService<SoftwareEntity> {

    private final SoftwareRepository softwareRepository;
    private final EquipmentSoftwareRepository equipmentSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<SoftwareEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(softwareRepository.findAll().spliterator(), false).toList();
        }
        return softwareRepository.findByNameContainingIgnoreCase(name);
    }

    public List<SoftwareEntity> getAll() {
        return softwareRepository.findAll(); // или аналогичный метод
    }

    @Transactional(readOnly = true)
    public Page<SoftwareEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return softwareRepository.findAll(pageable);
        }
        return softwareRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public List<SoftwareEntity> searchByName(String name) {
        return softwareRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<SoftwareEntity> filterByVersion(String version) {
        return softwareRepository.findByVersionContainingIgnoreCase(version);
    }

    @Transactional(readOnly = true)
    public SoftwareEntity get(long id) {
        return softwareRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(SoftwareEntity.class, id));
    }

    @Transactional
    public SoftwareEntity create(SoftwareEntity entity) {
        validate(entity, null);
        SoftwareEntity createdEntity = softwareRepository.save(entity);

        // Отправка уведомлений о добавлении нового ПО
        String message = String.format("Добавлено новое ПО: %s (версия: %s)",
                createdEntity.getName(), createdEntity.getVersion());
        notificationService.sendNotificationToAdmins(message);

        return createdEntity;
    }

    @Transactional
    public SoftwareEntity update(long id, SoftwareEntity dto) {
        validate(dto, id);
        SoftwareEntity existing = get(id);

        // Сохраняем старые значения для уведомления
        String oldName    = existing.getName();
        String oldVersion = existing.getVersion();

        existing.setName(dto.getName());
        existing.setVersion(dto.getVersion());
        existing.setDescription(dto.getDescription());
        existing.setType(dto.getType());

        SoftwareEntity updated = softwareRepository.save(existing);

        // Уведомление об обновлении
        String msgUpdate = String.format(
                "Изменено ПО: %s (версия: %s) → %s (версия: %s)",
                oldName, oldVersion,
                updated.getName(), updated.getVersion()
        );
        notificationService.sendNotificationToAdmins(msgUpdate);

        return updated;
    }

    @Transactional
    public SoftwareEntity delete(Long id) {
        SoftwareEntity software = softwareRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new FileNotFoundException("ПО не найдено"));

        // Сохраняем для уведомления
        String name    = software.getName();
        String version = software.getVersion();

        // Удаляем связи с этим ПО из всех EquipmentSoftwareEntity
        List<EquipmentSoftwareEntity> affected =
                equipmentSoftwareRepo.findAllBySoftwaresContaining(software);
        for (EquipmentSoftwareEntity es : affected) {
            es.getSoftwares().remove(software);
            if (es.getSoftwares().isEmpty()) {
                equipmentSoftwareRepo.delete(es);
            } else {
                equipmentSoftwareRepo.save(es);
            }
        }

        // Удаляем само ПО
        softwareRepository.delete(software);

        // Уведомление об удалении
        String msgDelete = String.format(
                "Удалено ПО: %s (версия: %s)",
                name, version
        );
        notificationService.sendNotificationToAdmins(msgDelete);

        return software;
    }

    @Override
    protected void validate(SoftwareEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Software entity is null");
        }

        validateStringField(entity.getName(), "Software name");
        validateStringField(entity.getVersion(), "Software version");
        validateStringField(entity.getDescription(), "Software description");

        if (entity.getType() == null) {
            throw new IllegalArgumentException("Software type must not be null");
        }

        boolean exists = softwareRepository.findAll().stream()
                .anyMatch(e ->
                        e.getName().equalsIgnoreCase(entity.getName()) &&
                                e.getVersion().equalsIgnoreCase(entity.getVersion()) &&
                                e.getDescription().equalsIgnoreCase(entity.getDescription()) &&
                                e.getType() == entity.getType() &&
                                (id == null || !e.getId().equals(id))
                );

        if (exists) {
            throw new IllegalArgumentException(String.format(
                    "ПО с названием '%s', версией '%s', описанием и типом '%s' уже существует",
                    entity.getName(), entity.getVersion(), entity.getType().name()
            ));
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getEquipmentsUsingSoftware(Long softwareId) {
        SoftwareEntity software = get(softwareId);
        return software.getEquipmentSoftwares().stream()
                .map(es -> es.getEquipment().getId())
                .toList();
    }

}

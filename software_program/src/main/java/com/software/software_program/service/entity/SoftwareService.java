package com.software.software_program.service.entity;

import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.repository.EquipmentSoftwareRepository;
import com.software.software_program.repository.SoftwareRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
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
    private final EquipmentSoftwareRepository classroomSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<SoftwareEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(softwareRepository.findAll().spliterator(), false).toList();
        }
        return softwareRepository.findByNameContainingIgnoreCase(name);
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
        notificationService.sendNotificationToAll(message);

        return createdEntity;
    }

    @Transactional
    public SoftwareEntity update(long id, SoftwareEntity entity) {
        validate(entity, id);
        SoftwareEntity existsEntity = get(id);

        existsEntity.setName(entity.getName());
        existsEntity.setVersion(entity.getVersion());
        existsEntity.setDescription(entity.getDescription());
        existsEntity.setType(entity.getType());

        syncEquipmentSoftwares(existsEntity, entity.getEquipmentSoftwares());
        syncSoftwareRequests(existsEntity, entity.getSoftwareRequests());

        return softwareRepository.save(existsEntity);
    }
    @Transactional
    public SoftwareEntity delete(long id) {
        SoftwareEntity existsEntity = get(id);
        softwareRepository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(SoftwareEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Software entity is null");
        }
        validateStringField(entity.getName(), "Software name");
        validateStringField(entity.getVersion(), "Software version");
        validateStringField(entity.getDescription(), "Software description");

        final Optional<SoftwareEntity> existingUser = softwareRepository.findByNameIgnoreCase(entity.getName());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("User with email %s already exists", entity.getName())
            );
        }
    }

    private boolean matchesFilters(SoftwareEntity software, String name, String version, String description) {
        boolean matchesName = name == null || software.getName().toLowerCase().contains(name.toLowerCase());
        boolean matchesVersion = version == null || software.getVersion().toLowerCase().contains(version.toLowerCase());
        boolean matchesDescription = description == null || software.getDescription().toLowerCase().contains(description.toLowerCase());
        return matchesName && matchesVersion && matchesDescription;
    }

    @Transactional(readOnly = true)
    public List<Long> getEquipmentsUsingSoftware(Long softwareId) {
        SoftwareEntity software = get(softwareId);
        return software.getEquipmentSoftwares().stream()
                .map(es -> es.getEquipment().getId())
                .toList();
    }

    private void syncSoftwareRequests(SoftwareEntity software, Set<SoftwareRequestEntity> updatedRequests) {
        Set<SoftwareRequestEntity> currentRequests = new HashSet<>(software.getSoftwareRequests());

        Set<SoftwareRequestEntity> toRemove = currentRequests.stream()
                .filter(req -> updatedRequests.stream().noneMatch(u -> u.getId().equals(req.getId())))
                .collect(Collectors.toSet());

        for (SoftwareRequestEntity request : toRemove) {
            software.removeSoftwareRequest(request);
        }

        for (SoftwareRequestEntity request : updatedRequests) {
            boolean exists = currentRequests.stream().anyMatch(existing -> existing.getId().equals(request.getId()));
            if (!exists) {
                software.addSoftwareRequest(request);
            }
        }
    }

    private void syncEquipmentSoftwares(SoftwareEntity software, Set<EquipmentSoftwareEntity> updatedSoftwares) {
        Set<EquipmentSoftwareEntity> currentSoftwares = new HashSet<>(software.getEquipmentSoftwares());

        // Удаление старых
        Set<EquipmentSoftwareEntity> toRemove = currentSoftwares.stream()
                .filter(existing -> updatedSoftwares.stream().noneMatch(updated ->
                        updated.getEquipment().getId().equals(existing.getEquipment().getId())))
                .collect(Collectors.toSet());

        for (EquipmentSoftwareEntity es : toRemove) {
            software.removeEquipmentSoftware(es);
        }

        // Добавление новых
        for (EquipmentSoftwareEntity updated : updatedSoftwares) {
            boolean exists = currentSoftwares.stream().anyMatch(existing ->
                    existing.getEquipment().getId().equals(updated.getEquipment().getId()));
            if (!exists) {
                software.addEquipmentSoftware(updated);
            }
        }
    }


}

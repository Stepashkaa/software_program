package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.ClassroomSoftwareRepository;
import com.software.software_program.repository.SoftwareRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SoftwareService extends AbstractEntityService<SoftwareEntity> {

    private final SoftwareRepository softwareRepository;
    private final ClassroomSoftwareRepository classroomSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public Page<SoftwareEntity> getAllByFilters(
            String name,
            String version,
            String description,
            Pageable pageable
    ) {
        return softwareRepository.findAllByFilters(name, version, description, pageable);
    }

    @Transactional(readOnly = true)
    public List<SoftwareEntity> getAll(String name, String version, String description) {
        return StreamSupport.stream(softwareRepository.findAll().spliterator(), false)
                .filter(software -> matchesFilters(software, name, version, description))
                .collect(Collectors.toList());
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
        notificationService.sendNotification(message, null); // Уведомление для администраторов

        return createdEntity;
    }

    @Transactional
    public SoftwareEntity update(long id, SoftwareEntity entity) {
        validate(entity, id);
        SoftwareEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setVersion(entity.getVersion());
        existsEntity.setDescription(entity.getDescription());
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
    public List<ClassroomSoftwareEntity> getClassroomsUsingSoftware(Long softwareId) {
        SoftwareEntity software = get(softwareId);
        return software.getClassroomSoftwares().stream().toList();
    }
}

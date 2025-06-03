package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.ClassroomSoftwareRepository;
import com.software.software_program.repository.SoftwareRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        validate(entity, true);
        SoftwareEntity createdEntity = softwareRepository.save(entity);

        // Отправка уведомлений о добавлении нового ПО
        String message = String.format("Добавлено новое ПО: %s (версия: %s)",
                createdEntity.getName(), createdEntity.getVersion());
        notificationService.sendNotification(message, null); // Уведомление для администраторов

        return createdEntity;
    }

    @Transactional
    public SoftwareEntity update(long id, SoftwareEntity entity) {
        validate(entity, false);
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
    protected void validate(SoftwareEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Software entity is null");
        }
        validateStringField(entity.getName(), "Software name");
        validateStringField(entity.getVersion(), "Software version");
        validateStringField(entity.getDescription(), "Software description");

        if (uniqueCheck && softwareRepository.findByNameIgnoreCase(entity.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Software with name %s already exists", entity.getName())
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

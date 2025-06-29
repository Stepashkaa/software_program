package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.FacultyEntity;
import com.software.software_program.repository.ClassroomRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class ClassroomService extends AbstractEntityService<ClassroomEntity> {

    private final ClassroomRepository classroomRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(classroomRepository.findAll().spliterator(), false).toList();
        }
        return classroomRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<ClassroomEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return classroomRepository.findAll(pageable);
        }
        return classroomRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getAll() {
        return StreamSupport.stream(classroomRepository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public ClassroomEntity get(long id) {
        return classroomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ClassroomEntity.class, id));
    }

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getByIds(List<Long> ids) {
        return StreamSupport.stream(classroomRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassroomEntity create(ClassroomEntity entity) {
        validate(entity, null);
        ClassroomEntity createdEntity = classroomRepository.save(entity);

        String message = String.format(
                "Добавлена новая аудитория: %s",
                createdEntity.getName()
        );
        notificationService.sendNotificationToAdmins(message);

        return createdEntity;
    }


    @Transactional
    public ClassroomEntity update(long id, ClassroomEntity entity) {
        validate(entity, id);
        ClassroomEntity existsEntity = get(id);

        String oldName = existsEntity.getName();

        existsEntity.setName(entity.getName());
        existsEntity.setCapacity(entity.getCapacity());
        existsEntity.setDepartment(entity.getDepartment());
        syncEquipments(existsEntity, entity.getEquipments());

        ClassroomEntity updated = classroomRepository.save(existsEntity);

        String msgUpdate = String.format(
                "Изменена аудитория: %s → %s",
                oldName,
                updated.getName()
        );
        notificationService.sendNotificationToAdmins(msgUpdate);

        return updated;
    }

    @Transactional
    public ClassroomEntity delete(long id) {
        ClassroomEntity existsEntity = get(id);
        String name = existsEntity.getName();

        classroomRepository.delete(existsEntity);

        String msgDelete = String.format("Удалена аудитория: %s", name);
        notificationService.sendNotificationToAdmins(msgDelete);

        return existsEntity;
    }

    @Override
    protected void validate(ClassroomEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Classroom entity is null");
        }
        validateStringField(entity.getName(), "Classroom name");
        if (entity.getCapacity() == null || entity.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be a positive number");
        }
        if (entity.getDepartment() == null) {
            throw new IllegalArgumentException("Department must not be null");
        }
        final Optional<ClassroomEntity> existingEntity = classroomRepository.findByNameIgnoreCase(entity.getName());
        if (existingEntity.isPresent() && !existingEntity.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Classroom with name %s already exists", entity.getName())
            );
        }
    }

    private void syncEquipments(ClassroomEntity existsEntity, Set<EquipmentEntity> updatedEquipments) {
        Set<EquipmentEntity> currentEquipments = new HashSet<>(existsEntity.getEquipments());
        Set<EquipmentEntity> updatedEquipmentsCopy = new HashSet<>(updatedEquipments);

        Set<EquipmentEntity> equipmentsToRemove = currentEquipments.stream()
                .filter(classroom -> !updatedEquipmentsCopy.contains(classroom))
                .collect(Collectors.toSet());

        for (EquipmentEntity equipment : equipmentsToRemove) {
            existsEntity.removeEquipment(equipment);
        }

        for (EquipmentEntity equipment : updatedEquipmentsCopy) {
            if (!currentEquipments.contains(equipment)) {
                existsEntity.addEquipment(equipment);
            }
        }
    }
}
package com.software.software_program.service.entity;

import com.software.software_program.model.entity.*;
//import com.software.software_program.model.entity.ReportEntity;
import com.software.software_program.repository.EquipmentSoftwareRepository;
import com.software.software_program.repository.DepartmentRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DepartmentService extends AbstractEntityService<DepartmentEntity> {
    private final DepartmentRepository repository;
    private final EquipmentSoftwareRepository classroomSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<DepartmentEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        }
        return repository.findByNameContainingIgnoreCase(name);
    }
    @Transactional(readOnly = true)
    public List<DepartmentEntity> getAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public Page<DepartmentEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public List<DepartmentEntity> getByIds(List<Long> ids) {
        return repository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public DepartmentEntity get(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(DepartmentEntity.class, id));
    }

    @Transactional
    public DepartmentEntity create(DepartmentEntity entity) {
        validate(entity, null);
        DepartmentEntity createdEntity = repository.save(entity);

        String message = String.format(
                "Добавлена новая кафедра: %s",
                createdEntity.getName()
        );
        notificationService.sendNotificationToAdmins(message);

        return createdEntity;
    }

    @Transactional
    public DepartmentEntity update(long id, DepartmentEntity dto) {
        validate(dto, id);
        DepartmentEntity existing = get(id);

        // запомним старое имя (если переименовали)
        String oldName = existing.getName();

        existing.setName(dto.getName());

        if (dto.getFaculty() == null) {
            throw new IllegalArgumentException("Faculty must not be null");
        }
        existing.setFaculty(dto.getFaculty());

        if (dto.getHead() == null) {
            throw new IllegalArgumentException("Head of the department must not be null");
        }
        existing.setHead(dto.getHead());

        if (dto.getClassrooms() != null) {
            syncClassrooms(existing, dto.getClassrooms());
        }

        DepartmentEntity updated = repository.save(existing);

        String msg = String.format(
                "Изменена кафедра: %s → %s",
                oldName,
                updated.getName()
        );
        notificationService.sendNotificationToAdmins(msg);

        return updated;
    }

    @Transactional
    public DepartmentEntity delete(long id) {
        DepartmentEntity existsEntity = get(id);
        String name = existsEntity.getName();
        repository.delete(existsEntity);

        String msg = String.format("Удалена кафедра: %s", name);
        notificationService.sendNotificationToAdmins(msg);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getClassrooms(Long departmentId) {
        return get(departmentId).getClassrooms().stream().toList();
    }

    @Transactional(readOnly = true)
    public List<SoftwareEntity> getSoftwareUsed(Long departmentId, int months) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(months);
        return classroomSoftwareRepo.findUniqueSoftwareByDepartmentAndPeriod(
                departmentId, start, end
        );
    }

    @Override
    protected void validate(DepartmentEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Department entity is null");
        }
        validateStringField(entity.getName(), "Department name");
        final Optional<DepartmentEntity> existingUser = repository.findByNameIgnoreCase(entity.getName());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Department name %s already exists", entity.getName())
            );
        }
        if (entity.getFaculty() == null) {
            throw new IllegalArgumentException("Faculty must not be null");
        }
        if (entity.getHead() == null) {
            throw new IllegalArgumentException("Head of the department must not be null");
        }
    }

    private void syncClassrooms(DepartmentEntity existsEntity, Set<ClassroomEntity> updatedClassrooms) {
        Set<ClassroomEntity> currentClassrooms = new HashSet<>(existsEntity.getClassrooms());
        Set<ClassroomEntity> updatedClassroomsCopy = new HashSet<>(updatedClassrooms);

        // Находим аудитории для удаления
        Set<ClassroomEntity> classroomsToRemove = currentClassrooms.stream()
                .filter(classroom -> !updatedClassroomsCopy.contains(classroom))
                .collect(Collectors.toSet());

        // Удаляем найденные аудитории
        for (ClassroomEntity classroom : classroomsToRemove) {
            existsEntity.removeClassroom(classroom);
        }

        // Добавляем новые или обновляем существующие аудитории
        for (ClassroomEntity classroom : updatedClassroomsCopy) {
            if (!currentClassrooms.contains(classroom)) {
                existsEntity.addClassroom(classroom);
            }
        }
    }

}
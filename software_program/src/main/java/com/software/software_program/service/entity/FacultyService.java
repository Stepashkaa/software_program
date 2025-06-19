package com.software.software_program.service.entity;

import com.software.software_program.model.entity.*;
import com.software.software_program.repository.FacultyRepository;
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
public class FacultyService extends AbstractEntityService<FacultyEntity> {

    private final FacultyRepository facultyRepository;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<FacultyEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(facultyRepository.findAll().spliterator(), false).toList();
        }
        return facultyRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<FacultyEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return facultyRepository.findAll(pageable);
        }
        return facultyRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public FacultyEntity get(long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(FacultyEntity.class, id));
    }

    @Transactional
    public FacultyEntity create(FacultyEntity entity) {
        validate(entity, null);
        FacultyEntity createdEntity = facultyRepository.save(entity);

        // Отправка уведомлений о добавлении нового факультета
        String message = String.format(
                "Добавлен новый факультет: %s",
                createdEntity.getName()
        );
        notificationService.sendNotificationToAdmins(message);

        return createdEntity;
    }


    @Transactional
    public FacultyEntity update(long id, FacultyEntity entity) {
        validate(entity, id);
        FacultyEntity existsEntity = get(id);

        String oldName = existsEntity.getName();

        if (entity.getName() != null) {
            existsEntity.setName(entity.getName());
        }
        if (entity.getDepartments() != null) {
            syncDepartments(existsEntity, entity.getDepartments());
        }

        FacultyEntity updated = facultyRepository.save(existsEntity);
        String msgUpdate = String.format(
                "Изменён факультет: %s → %s",
                oldName,
                updated.getName()
        );
        notificationService.sendNotificationToAdmins(msgUpdate);

        return updated;
    }

    @Transactional
    public FacultyEntity delete(long id) {
        FacultyEntity existsEntity = get(id);
        String name = existsEntity.getName();
        facultyRepository.delete(existsEntity);
        String msgDelete = String.format(
                "Удалён факультет: %s",
                name
        );
        notificationService.sendNotificationToAdmins(msgDelete);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<DepartmentEntity> getDepartments(Long facultyId) {
        return get(facultyId).getDepartments().stream().toList();
    }

    @Override
    protected void validate(FacultyEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Faculty entity is null");
        }
        validateStringField(entity.getName(), "Faculty name");

        Optional<FacultyEntity> existing = facultyRepository.findByNameIgnoreCase(entity.getName());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Faculty name '%s' already exists", entity.getName())
            );
        }
    }

    private void syncDepartments(FacultyEntity existsEntity, Set<DepartmentEntity> updatedDepartments) {
        if (updatedDepartments == null || updatedDepartments.isEmpty()) {
            // Если список кафедр пуст, ничего не делаем
            return;
        }

        // Находим кафедры для удаления
        Set<DepartmentEntity> departmentsToRemove = existsEntity.getDepartments().stream()
                .filter(department -> !updatedDepartments.contains(department))
                .collect(Collectors.toSet());

        // Удаляем найденные кафедры
        for (DepartmentEntity department : departmentsToRemove) {
            existsEntity.removeDepartment(department);
        }

        // Добавляем новые или обновляем существующие кафедры
        for (DepartmentEntity department : updatedDepartments) {
            if (!existsEntity.getDepartments().contains(department)) {
                existsEntity.addDepartment(department);
            }
        }
    }

}
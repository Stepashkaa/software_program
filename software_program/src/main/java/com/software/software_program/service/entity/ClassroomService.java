package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.repository.ClassroomRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ClassroomService extends AbstractEntityService<ClassroomEntity> {

    private final ClassroomRepository classroomRepository;

    @Transactional(readOnly = true)
    public Page<ClassroomEntity> getAllByFilters(
            String name,
            Integer capacity,
            Long departmentId,
            Pageable pageable
    ) {
        return classroomRepository.findAllByFilters(name, capacity, departmentId, pageable);
    }

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getAll(String name) {
        if (name == null || name.isEmpty()) {
            return StreamSupport.stream(classroomRepository.findAll().spliterator(), false).toList();
        }
        return classroomRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<ClassroomEntity> getAll(String name, int page, int size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        if (name == null || name.isBlank()) {
            return classroomRepository.findAll(pageRequest);
        }
        return classroomRepository.findByNameContainingIgnoreCase(name, pageRequest);
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
        validate(entity, true);
        return classroomRepository.save(entity);
    }

    @Transactional
    public ClassroomEntity update(long id, ClassroomEntity entity) {
        validate(entity, false);
        ClassroomEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setCapacity(entity.getCapacity());
        existsEntity.setDepartment(entity.getDepartment());
        syncClassroomSoftwares(existsEntity, entity.getClassroomSoftwares());
        syncEquipments(existsEntity, entity.getEquipments());
        return classroomRepository.save(existsEntity);
    }

    @Transactional
    public ClassroomEntity delete(long id) {
        ClassroomEntity existsEntity = get(id);
        classroomRepository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(ClassroomEntity entity, boolean uniqueCheck) {
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
        if (uniqueCheck && classroomRepository.findByNameIgnoreCase(entity.getName()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Classroom with name %s already exists", entity.getName())
            );
        }
    }

    private void syncClassroomSoftwares(ClassroomEntity existsEntity, Set<ClassroomSoftwareEntity> updatedClassroomSoftwares) {
        existsEntity.getClassroomSoftwares().removeIf(classroomSoftware -> !updatedClassroomSoftwares.contains(classroomSoftware));

        for (ClassroomSoftwareEntity classroomSoftware : updatedClassroomSoftwares) {
            if (!existsEntity.getClassroomSoftwares().contains(classroomSoftware)) {
                existsEntity.addClassroomSoftware(classroomSoftware);
            }
        }
    }

    private void syncEquipments(ClassroomEntity existsEntity, Set<EquipmentEntity> updatedEquipments) {
        existsEntity.getEquipments().removeIf(equipment -> !updatedEquipments.contains(equipment));

        for (EquipmentEntity equipment : updatedEquipments) {
            if (!existsEntity.getEquipments().contains(equipment)) {
                existsEntity.addEquipment(equipment);
            }
        }
    }
}
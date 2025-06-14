package com.software.software_program.service.entity;

import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.SoftwareRequestEntity;
import com.software.software_program.repository.EquipmentRepository;
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
public class EquipmentService extends AbstractEntityService<EquipmentEntity> {

    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public List<EquipmentEntity> getAll(String name) {
        if (name == null || name.isBlank()) {
            return StreamSupport.stream(equipmentRepository.findAll().spliterator(), false).toList();
        }
        return equipmentRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<EquipmentEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return equipmentRepository.findAll(pageable);
        }
        return equipmentRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public EquipmentEntity get(long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EquipmentEntity.class, id));
    }

    @Transactional
    public EquipmentEntity create(EquipmentEntity entity) {
        validate(entity, null);
        return equipmentRepository.save(entity);
    }

    @Transactional
    public EquipmentEntity update(long id, EquipmentEntity entity) {
        validate(entity, id);
        EquipmentEntity existsEntity = get(id);

        existsEntity.setName(entity.getName());
        existsEntity.setType(entity.getType());
        existsEntity.setSerialNumber(entity.getSerialNumber());
        existsEntity.setClassroom(entity.getClassroom());

        syncEquipmentSoftwares(existsEntity, entity.getEquipmentSoftwares());
        syncRequests(existsEntity, entity.getSoftwareRequests());

        return equipmentRepository.save(existsEntity);
    }


    @Transactional(readOnly = true)
    public List<EquipmentSoftwareEntity> getInstalledSoftware(long equipmentId) {
        EquipmentEntity equipment = get(equipmentId);
        return equipment.getEquipmentSoftwares().stream().toList();
    }

    @Transactional(readOnly = true)
    public List<EquipmentEntity> getByClassroomId(Long classroomId) {
        return equipmentRepository.findAll().stream()
                .filter(e -> e.getClassroom().getId().equals(classroomId))
                .toList();
    }

    @Transactional
    public EquipmentEntity delete(long id) {
        EquipmentEntity existsEntity = get(id);
        equipmentRepository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(EquipmentEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("Equipment entity is null");
        }
        validateStringField(entity.getName(), "Equipment name");
        validateStringField(entity.getType(), "Equipment type");
        validateStringField(entity.getSerialNumber(), "Equipment serial number");

        final Optional<EquipmentEntity> existingUser = equipmentRepository.findByNameIgnoreCase(entity.getName());
        if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("Equipment %s already exists", entity.getName())
            );
        }

        if (entity.getClassroom() == null) {
            throw new IllegalArgumentException("Classroom must not be null");
        }
    }

    private boolean matchesFilters(
            EquipmentEntity equipment,
            String name,
            String type,
            String serialNumber
    ) {
        boolean matchesName = name == null || equipment.getName().toLowerCase().contains(name.toLowerCase());
        boolean matchesType = type == null || equipment.getType().toLowerCase().contains(type.toLowerCase());
        boolean matchesSerialNumber = serialNumber == null || equipment.getSerialNumber().toLowerCase().contains(serialNumber.toLowerCase());
        return matchesName && matchesType && matchesSerialNumber;
    }

    private void syncEquipmentSoftwares(EquipmentEntity equipment, Set<EquipmentSoftwareEntity> updatedSoftwares) {
        Set<EquipmentSoftwareEntity> currentSoftwares = new HashSet<>(equipment.getEquipmentSoftwares());

        // Удаление устаревших связей
        Set<EquipmentSoftwareEntity> toRemove = currentSoftwares.stream()
                .filter(existing -> updatedSoftwares.stream().noneMatch(updated ->
                        existing.getSoftwares().equals(updated.getSoftwares())))
                .collect(Collectors.toSet());

        for (EquipmentSoftwareEntity es : toRemove) {
            equipment.removeSoftware(es);
        }

        // Добавление новых связей
        for (EquipmentSoftwareEntity updated : updatedSoftwares) {
            boolean exists = currentSoftwares.stream().anyMatch(existing ->
                    existing.getSoftwares().equals(updated.getSoftwares()));
            if (!exists) {
                equipment.addSoftware(updated);
            }
        }
    }


    private void syncRequests(EquipmentEntity equipment, Set<SoftwareRequestEntity> updatedRequests) {
        Set<SoftwareRequestEntity> currentRequests = new HashSet<>(equipment.getSoftwareRequests());

        Set<SoftwareRequestEntity> toRemove = currentRequests.stream()
                .filter(req -> updatedRequests.stream().noneMatch(u -> u.getId().equals(req.getId())))
                .collect(Collectors.toSet());

        for (SoftwareRequestEntity request : toRemove) {
            equipment.removeSoftwareRequest(request);
        }

        for (SoftwareRequestEntity request : updatedRequests) {
            boolean exists = currentRequests.stream().anyMatch(existing -> existing.getId().equals(request.getId()));
            if (!exists) {
                equipment.addSoftwareRequest(request);
            }
        }
    }

}
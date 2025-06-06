package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.repository.EquipmentRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EquipmentService extends AbstractEntityService<EquipmentEntity> {

    private final EquipmentRepository equipmentRepository;

    @Transactional(readOnly = true)
    public Page<EquipmentEntity> getAll(String type, String serialNumber, Long classroomId, Pageable pageable) {
        return equipmentRepository.findAllByFilters(type, serialNumber, classroomId, pageable);
    }

    @Transactional(readOnly = true)
    public List<EquipmentEntity> getAll(String type, String serialNumber, Long classroomId) {
        if (type == null && serialNumber == null && classroomId == null) {
            return equipmentRepository.findAll();
        }
        return equipmentRepository.findAll().stream()
                .filter(equipment -> matchesFilters(equipment, type, serialNumber, classroomId))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipmentEntity get(long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(EquipmentEntity.class, id));
    }

    @Transactional
    public EquipmentEntity create(EquipmentEntity entity) {
        validate(entity, true);
        return equipmentRepository.save(entity);
    }

    @Transactional
    public EquipmentEntity update(long id, EquipmentEntity entity) {
        validate(entity, false);
        EquipmentEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setType(entity.getType());
        existsEntity.setSerialNumber(entity.getSerialNumber());
        existsEntity.setClassroom(entity.getClassroom());
        return equipmentRepository.save(existsEntity);
    }

    @Transactional
    public EquipmentEntity delete(long id) {
        EquipmentEntity existsEntity = get(id);
        equipmentRepository.delete(existsEntity);
        return existsEntity;
    }

    @Override
    protected void validate(EquipmentEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Equipment entity is null");
        }
        validateStringField(entity.getName(), "Equipment name");
        validateStringField(entity.getType(), "Equipment type");
        validateStringField(entity.getSerialNumber(), "Equipment serial number");

        if (uniqueCheck && equipmentRepository.findBySerialNumberIgnoreCase(entity.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException(
                    String.format("Equipment with serial number %s already exists", entity.getSerialNumber())
            );
        }
        if (entity.getClassroom() == null) {
            throw new IllegalArgumentException("Classroom must not be null");
        }
    }

    private boolean matchesFilters(EquipmentEntity equipment, String type, String serialNumber, Long classroomId) {
        boolean matchesType = type == null || equipment.getType().toLowerCase().contains(type.toLowerCase());
        boolean matchesSerialNumber = serialNumber == null || equipment.getSerialNumber().toLowerCase().contains(serialNumber.toLowerCase());
        boolean matchesClassroomId = classroomId == null || equipment.getClassroom().getId().equals(classroomId);
        return matchesType && matchesSerialNumber && matchesClassroomId;
    }
}
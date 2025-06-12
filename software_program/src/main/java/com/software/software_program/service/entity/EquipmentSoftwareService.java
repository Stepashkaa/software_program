package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.EquipmentSoftwareRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentSoftwareService extends AbstractEntityService<EquipmentSoftwareEntity> {

    private final EquipmentSoftwareRepository equipmentSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional
    public EquipmentSoftwareEntity create(EquipmentSoftwareEntity entity) {
        validate(entity, null);
        validate(entity.getEquipment(), entity.getSoftware(), entity.getInstallationDate());
        EquipmentSoftwareEntity createdEntity = equipmentSoftwareRepo.save(entity);

        String message = String.format("Программное обеспечение '%s' установлено в аудитории '%s'",
                entity.getSoftware().getName(), entity.getEquipment().getName());
        notificationService.sendNotificationToAll(message);

        return createdEntity;
    }

    @Transactional(readOnly = true)
    public List<EquipmentSoftwareEntity> getAll() {
        return equipmentSoftwareRepo.findAll();
    }

    @Transactional(readOnly = true)
    public EquipmentSoftwareEntity get(long id) {
        return equipmentSoftwareRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(EquipmentSoftwareEntity.class, id));
    }

    @Transactional
    public EquipmentSoftwareEntity update(long id, EquipmentSoftwareEntity entity) {
        validate(entity, id);
        validate(entity.getEquipment(), entity.getSoftware(), entity.getInstallationDate());
        EquipmentSoftwareEntity existingEntity = get(id);

        existingEntity.setEquipment(entity.getEquipment());
        existingEntity.setSoftware(entity.getSoftware());
        existingEntity.setInstallationDate(entity.getInstallationDate());

        return equipmentSoftwareRepo.save(existingEntity);
    }

    @Transactional
    public EquipmentSoftwareEntity delete(long id) {
        EquipmentSoftwareEntity existsEntity = get(id);
        equipmentSoftwareRepo.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<EquipmentSoftwareEntity> getClassroomSoftwareReport(Long classroomId, LocalDate start, LocalDate end) {
        return equipmentSoftwareRepo.findClassroomSoftwareReport(classroomId, start, end);
    }

    @Transactional(readOnly = true)
    public List<EquipmentSoftwareEntity> getDepartmentSoftwareReport(Long departmentId, LocalDate start, LocalDate end) {
        return equipmentSoftwareRepo.findDepartmentSoftwareReport(departmentId, start, end);
    }

    public List<SoftwareEntity> getUniqueSoftwareByDepartmentAndPeriod(Long departmentId, LocalDate start, LocalDate end) {
        return equipmentSoftwareRepo.findUniqueSoftwareByDepartmentAndPeriod(departmentId, start, end);
    }

    @Override
    protected void validate(EquipmentSoftwareEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("EquipmentSoftware entity is null");
        }
        validate(entity.getEquipment(), entity.getSoftware(), entity.getInstallationDate());

        final Optional<EquipmentSoftwareEntity> existingEntity = equipmentSoftwareRepo.findByEquipmentAndSoftware(
                entity.getEquipment(), entity.getSoftware()
        );

        if (existingEntity.isPresent() && !existingEntity.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("EquipmentSoftware with equipment '%s' and software '%s' already exists",
                            entity.getEquipment().getName(), entity.getSoftware().getName())
            );
        }
    }

    private void validate(EquipmentEntity equipment, SoftwareEntity software, Date installationDate) {
        if (equipment == null) {
            throw new IllegalArgumentException("Classroom must not be null");
        }
        if (software == null) {
            throw new IllegalArgumentException("Software must not be null");
        }
        if (installationDate == null || installationDate.after(new Date())){
            throw new IllegalArgumentException("Installation date must not be null or in the future");
        }
    }
}
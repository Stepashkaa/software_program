package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.EquipmentEntity;
import com.software.software_program.model.entity.EquipmentSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.EquipmentSoftwareRepository;
import com.software.software_program.core.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentSoftwareService extends AbstractEntityService<EquipmentSoftwareEntity> {

    private final EquipmentSoftwareRepository equipmentSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional
    public EquipmentSoftwareEntity create(EquipmentSoftwareEntity entity) {
        validate(entity, null);
        validate(entity.getEquipment(), entity.getSoftwares(), entity.getInstallationDate());

        EquipmentSoftwareEntity createdEntity = equipmentSoftwareRepo.save(entity);

        String message = String.format(
                "ПО [%s] установлено на оборудование '%s' (аудитория: %s)",
                entity.getSoftwares().stream().map(SoftwareEntity::getName).toList(),
                entity.getEquipment().getName(),
                entity.getEquipment().getClassroom().getName()
        );
        notificationService.sendNotificationToAdmins(message);

        return createdEntity;
    }

    @Transactional
    public EquipmentSoftwareEntity update(long id, EquipmentSoftwareEntity dto) {
        // Ваши валидации
        validate(dto, id);
        validate(dto.getEquipment(), dto.getSoftwares(), dto.getInstallationDate());

        EquipmentSoftwareEntity existing = equipmentSoftwareRepo.findByIdWithAllRelations(id)
                .orElseThrow(() -> new NotFoundException(EquipmentSoftwareEntity.class, id));

        String oldEquipment = existing.getEquipment().getName();
        String oldClassroom = existing.getEquipment().getClassroom().getName();
        String oldSoftwares = existing.getSoftwares().stream()
                .map(SoftwareEntity::getName)
                .collect(Collectors.joining(", "));

        existing.setEquipment(dto.getEquipment());
        existing.setSoftwares(dto.getSoftwares());
        existing.setInstallationDate(dto.getInstallationDate());

        EquipmentSoftwareEntity updated = equipmentSoftwareRepo.save(existing);

        String newSoftwares = updated.getSoftwares().stream()
                .map(SoftwareEntity::getName)
                .collect(Collectors.joining(", "));

        String msg = String.format(
                "Изменена запись установки ПО на оборудование '%s' (аудитория: %s): [%s] → [%s]",
                oldEquipment,
                oldClassroom,
                oldSoftwares,
                newSoftwares
        );
        notificationService.sendNotificationToAdmins(msg);

        return updated;
    }


    @Transactional
    public EquipmentSoftwareEntity delete(long id) {
        EquipmentSoftwareEntity existing = equipmentSoftwareRepo.findByIdWithAllRelations(id)
                .orElseThrow(() -> new NotFoundException(EquipmentSoftwareEntity.class, id));

        String equipmentName = existing.getEquipment().getName();
        String classroomName = existing.getEquipment().getClassroom().getName();
        String softwares = existing.getSoftwares().stream()
                .map(SoftwareEntity::getName)
                .collect(Collectors.joining(", "));

        equipmentSoftwareRepo.delete(existing);

        String msg = String.format(
                "Удалена запись установки ПО [%s] с оборудования '%s' (аудитория: %s)",
                softwares,
                equipmentName,
                classroomName
        );
        notificationService.sendNotificationToAdmins(msg);

        return existing;
    }

    @Transactional(readOnly = true)
    public List<EquipmentSoftwareEntity> getAll() {
        return equipmentSoftwareRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Page<EquipmentSoftwareEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            return equipmentSoftwareRepo.findAll(pageable);
        }
        return equipmentSoftwareRepo.findByEquipmentNameContainingIgnoreCase(name, pageable);
    }

    @Transactional(readOnly = true)
    public EquipmentSoftwareEntity get(long id) {
        return equipmentSoftwareRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(EquipmentSoftwareEntity.class, id));
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

        if (entity.getEquipment() == null) {
            throw new IllegalArgumentException("Оборудование не указано");
        }

        if (entity.getSoftwares() == null || entity.getSoftwares().isEmpty()) {
            throw new IllegalArgumentException("Список ПО не должен быть пустым");
        }

        Date now = new Date();
        if (entity.getInstallationDate() == null || entity.getInstallationDate().after(now))
            throw new IllegalArgumentException("Дата установки не должна быть в будущем");

        // Проверка на точный дубликат: оборудование + набор ПО + дата
        List<EquipmentSoftwareEntity> allForEquipment = equipmentSoftwareRepo.findByEquipment(entity.getEquipment());

        Set<Long> newSoftwareIds = entity.getSoftwares().stream()
                .map(SoftwareEntity::getId)
                .collect(Collectors.toSet());

        for (EquipmentSoftwareEntity existing : allForEquipment) {
            Set<Long> existingSoftwareIds = existing.getSoftwares().stream()
                    .map(SoftwareEntity::getId)
                    .collect(Collectors.toSet());

            boolean sameDate = existing.getInstallationDate().equals(entity.getInstallationDate());

            if (existingSoftwareIds.equals(newSoftwareIds) && sameDate) {
                if (id == null || !existing.getId().equals(id)) {
                    throw new IllegalArgumentException(String.format(
                            "Уже существует запись с этим оборудованием, выбранным набором ПО и датой установки (ID: %d)",
                            existing.getId()
                    ));
                }
            }
        }
    }

    private void validate(EquipmentEntity equipment, Set<SoftwareEntity> softwares, Date installationDate) {
        if (equipment == null) {
            throw new IllegalArgumentException("Оборудование не указано");
        }
        if (softwares == null || softwares.isEmpty()) {
            throw new IllegalArgumentException("Список ПО не должен быть пустым");
        }
        if (installationDate == null || installationDate.after(new Date())) {
            throw new IllegalArgumentException("Дата установки не должна быть null или в будущем");
        }
    }

}
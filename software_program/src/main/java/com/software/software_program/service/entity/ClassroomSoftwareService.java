package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.ClassroomSoftwareRepository;
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
public class ClassroomSoftwareService extends AbstractEntityService<ClassroomSoftwareEntity> {

    private final ClassroomSoftwareRepository classroomSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional
    public ClassroomSoftwareEntity create(ClassroomSoftwareEntity entity) {
        validate(entity, null);
        validate(entity.getClassroom(), entity.getSoftware(), entity.getInstallationDate());
        ClassroomSoftwareEntity createdEntity = classroomSoftwareRepo.save(entity);

        String message = String.format("Программное обеспечение '%s' установлено в аудитории '%s'",
                entity.getSoftware().getName(), entity.getClassroom().getName());
        notificationService.sendNotification(message, null);

        return createdEntity;
    }

    @Transactional(readOnly = true)
    public List<ClassroomSoftwareEntity> getAll() {
        return classroomSoftwareRepo.findAll();
    }

    @Transactional(readOnly = true)
    public ClassroomSoftwareEntity get(long id) {
        return classroomSoftwareRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(ClassroomSoftwareEntity.class, id));
    }

    @Transactional
    public ClassroomSoftwareEntity update(long id, ClassroomSoftwareEntity entity) {
        validate(entity, id);
        validate(entity.getClassroom(), entity.getSoftware(), entity.getInstallationDate());
        ClassroomSoftwareEntity existingEntity = get(id);

        existingEntity.setClassroom(entity.getClassroom());
        existingEntity.setSoftware(entity.getSoftware());
        existingEntity.setInstallationDate(entity.getInstallationDate());

        return classroomSoftwareRepo.save(existingEntity);
    }

    @Transactional
    public ClassroomSoftwareEntity delete(long id) {
        ClassroomSoftwareEntity existsEntity = get(id);
        classroomSoftwareRepo.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<ClassroomSoftwareEntity> getClassroomSoftwareReport(Long classroomId, LocalDate start, LocalDate end) {
        return classroomSoftwareRepo.findClassroomSoftwareReport(classroomId, start, end);
    }

    @Transactional(readOnly = true)
    public List<ClassroomSoftwareEntity> getDepartmentSoftwareReport(Long departmentId, LocalDate start, LocalDate end) {
        return classroomSoftwareRepo.findDepartmentSoftwareReport(departmentId, start, end);
    }

    @Transactional(readOnly = true)
    public List<ClassroomSoftwareEntity> getUniqueSoftwareByDepartmentAndPeriod(Long departmentId, LocalDate start, LocalDate end) {
        return classroomSoftwareRepo.findUniqueSoftwareByDepartmentAndPeriod(departmentId, start, end);
    }

    @Override
    protected void validate(ClassroomSoftwareEntity entity, Long id) {
        if (entity == null) {
            throw new IllegalArgumentException("ClassroomSoftware entity is null");
        }
        validate(entity.getClassroom(), entity.getSoftware(), entity.getInstallationDate());

        final Optional<ClassroomSoftwareEntity> existingEntity = classroomSoftwareRepo.findByClassroomAndSoftware(
                entity.getClassroom(), entity.getSoftware()
        );

        if (existingEntity.isPresent() && !existingEntity.get().getId().equals(id)) {
            throw new IllegalArgumentException(
                    String.format("ClassroomSoftware with classroom '%s' and software '%s' already exists",
                            entity.getClassroom().getName(), entity.getSoftware().getName())
            );
        }
    }

    private void validate(ClassroomEntity classroom, SoftwareEntity software, Date installationDate) {
        if (classroom == null) {
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
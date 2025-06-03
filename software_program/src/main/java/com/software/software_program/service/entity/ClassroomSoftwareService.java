package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.ClassroomSoftwareRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomSoftwareService extends AbstractEntityService<ClassroomSoftwareEntity> {

    private final ClassroomSoftwareRepository classroomSoftwareRepo;
    private final NotificationService notificationService;

    @Transactional
    public ClassroomSoftwareEntity create(ClassroomEntity classroom, SoftwareEntity software, LocalDate installationDate) {
        validate(classroom, software, installationDate);
        ClassroomSoftwareEntity entity = new ClassroomSoftwareEntity(classroom, software, installationDate);
        ClassroomSoftwareEntity createdEntity = classroomSoftwareRepo.save(entity);

        // Отправка уведомлений о привязке ПО к аудитории
        String message = String.format("Программное обеспечение '%s' установлено в аудитории '%s'",
                software.getName(), classroom.getName());
        notificationService.sendNotification(message, null); // Уведомление для администраторов

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
    public ClassroomSoftwareEntity update(long id, ClassroomEntity classroom, SoftwareEntity software, LocalDate installationDate) {
        validate(classroom, software, installationDate);
        ClassroomSoftwareEntity existsEntity = get(id);
        existsEntity.setClassroom(classroom);
        existsEntity.setSoftware(software);
        existsEntity.setInstallationDate(installationDate);
        return classroomSoftwareRepo.save(existsEntity);
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
    public List<SoftwareEntity> getUniqueSoftwareByDepartmentAndPeriod(Long departmentId, LocalDate start, LocalDate end) {
        return classroomSoftwareRepo.findUniqueSoftwareByDepartmentAndPeriod(departmentId, start, end);
    }

    @Override
    protected void validate(ClassroomSoftwareEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("ClassroomSoftware entity is null");
        }
        validate(entity.getClassroom(), entity.getSoftware(), entity.getInstallationDate());
    }

    private void validate(ClassroomEntity classroom, SoftwareEntity software, LocalDate installationDate) {
        if (classroom == null) {
            throw new IllegalArgumentException("Classroom must not be null");
        }
        if (software == null) {
            throw new IllegalArgumentException("Software must not be null");
        }
        if (installationDate == null || installationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Installation date must not be null or in the future");
        }
    }
}
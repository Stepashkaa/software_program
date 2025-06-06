package com.software.software_program.service.entity;

import com.software.software_program.model.entity.ClassroomEntity;
import com.software.software_program.model.entity.ClassroomSoftwareEntity;
import com.software.software_program.model.entity.DepartmentEntity;
//import com.software.software_program.model.entity.ReportEntity;
import com.software.software_program.model.entity.SoftwareEntity;
import com.software.software_program.repository.ClassroomSoftwareRepository;
import com.software.software_program.repository.DepartmentRepository;
import com.software.software_program.core.eror.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class DepartmentService extends AbstractEntityService<DepartmentEntity> {
    private final DepartmentRepository repository;
    private final ClassroomSoftwareRepository classroomSoftwareRepo;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentEntity> getAll(String name) {
        if (name == null || name.isEmpty()) {
            return StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        }
        return repository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentEntity> getAll(String name, Pageable pageable) {
        if (name == null || name.isEmpty()) {
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
        validate(entity, true);
        return repository.save(entity);
    }

    @Transactional
    public DepartmentEntity update(long id, DepartmentEntity entity) {
        validate(entity, false);
        final DepartmentEntity existsEntity = get(id);
        existsEntity.setName(entity.getName());
        existsEntity.setFaculty(entity.getFaculty());
        syncClassrooms(existsEntity, entity.getClassrooms());
//        syncReports(existsEntity, entity.getReports());
        return repository.save(existsEntity);
    }

    @Transactional
    public DepartmentEntity delete(long id) {
        final DepartmentEntity existsEntity = get(id);
        repository.delete(existsEntity);
        return existsEntity;
    }

    @Transactional(readOnly = true)
    public List<ClassroomEntity> getClassrooms(Long departmentId) {
        return get(departmentId).getClassrooms().stream().toList();
    }

    @Transactional(readOnly = true)
    public List<ClassroomSoftwareEntity> getSoftwareUsed(Long departmentId, int months) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(months);
        return classroomSoftwareRepo.findUniqueSoftwareByDepartmentAndPeriod(
                departmentId, start, end
        );
    }

    @Override
    protected void validate(DepartmentEntity entity, boolean uniqueCheck) {
        if (entity == null) {
            throw new IllegalArgumentException("Department entity is null");
        }
        validateStringField(entity.getName(), "Department name");
        if (uniqueCheck) {
            if (repository.findByNameIgnoreCase(entity.getName()).isPresent()) {
                throw new IllegalArgumentException(
                        String.format("Department with name %s already exists", entity.getName())
                );
            }
        }
        if (entity.getFaculty() == null) {
            throw new IllegalArgumentException("Faculty must not be null");
        }
    }

    private void syncClassrooms(DepartmentEntity existsEntity, Set<ClassroomEntity> updatedClassrooms) {
        existsEntity.getClassrooms().removeIf(classroom -> !updatedClassrooms.contains(classroom));

        for (ClassroomEntity classroom : updatedClassrooms) {
            if (!existsEntity.getClassrooms().contains(classroom)) {
                existsEntity.addClassroom(classroom);
            }
        }
    }

//    private void syncReports(DepartmentEntity existsEntity, Set<ReportEntity> updatedReports) {
//        existsEntity.getReports().removeIf(report -> !updatedReports.contains(report));
//
//        for (ReportEntity report : updatedReports) {
//            if (!existsEntity.getReports().contains(report)) {
//                existsEntity.addReport(report);
//            }
//        }
//    }
}